using System;
using System.Collections;
using System.Collections.Generic;
using Random = UnityEngine.Random;
using UnityEngine;
#if UNITY_EDITOR
using UnityEditor;
#endif

/// <summary>
/// This enables the object to which the script is attached the
/// ability to spawn different kinds of loot
/// </summary>
public class LootSpawner : MonoBehaviour
{
    [System.Serializable]
    public class LootEntry
    {
        public int Weight = 1;
        public LootItem Item;
    }

    [System.Serializable]
    public class SpawnEvent
    {
        public LootEntry[] Entries;
    }

    private class InternalPercentageEntry
    {
        public LootEntry Entry;
        public float Percentage;
    }

    public CharacterData Player;
    public SpawnEvent[] Events;
    public AudioClip SpawnedClip;
    public AudioClip LootPickUpClip;


    public void SpawnLoot()
    {
        if (Player == null)
            return;

        Vector3 position = transform.position;
        var source = AudioManager.GetWorldSFXSource();
        source.transform.position = position;
        source.PlayOneShot(SpawnedClip);

        for (int i = 0; i < Events.Length; ++i)
        {
            SpawnEvent spawnEvent = Events[i];

            int weightSum = 0;
            foreach (var entry in spawnEvent.Entries)
            {
                weightSum += entry.Weight;
            }

            if (weightSum == 0)
                continue;

            List<InternalPercentageEntry> lookUpTable = new List<InternalPercentageEntry>();
            float previousPercent = 0f;
            foreach (var entry in spawnEvent.Entries)
            {
                float percent = entry.Weight / (float)weightSum;
                InternalPercentageEntry percentageEntry = new InternalPercentageEntry();
                percentageEntry.Entry = entry;
                percentageEntry.Percentage = previousPercent + percent;

                previousPercent = percentageEntry.Percentage;

                lookUpTable.Add(percentageEntry);
            }

            float range = Random.value;
            for (int k = 0; k < lookUpTable.Count; ++i)
            {
                if (range <= lookUpTable[k].Percentage)
                {
                    GameObject obj = new GameObject(lookUpTable[k].Entry.Item.itemName);
                    var loot = obj.AddComponent<Loot>();
                    loot.Item = lookUpTable[k].Entry.Item;
                    loot.Player = Player;
                    loot.PickupClip = LootPickUpClip;
                    loot.AggroRange = 2;
                    loot.Spawn(position);
                    break;
                }
            }
        }
    }
}

#if UNITY_EDITOR
[CustomEditor(typeof(LootSpawner))]
public class LootSpawnerEditor : Editor
{
    SerializedProperty m_PlayerProp;
    SerializedProperty m_SpawnSoundProp;
    SerializedProperty m_PickUpSoundProp;
    SerializedProperty m_SpawnEventProp;

    bool[] m_FoldoutInfos;

    int toDelete = -1;

    void OnEnable()
    {
        m_PlayerProp = serializedObject.FindProperty("Player");
        m_SpawnSoundProp = serializedObject.FindProperty("SpawnedClip");
        m_PickUpSoundProp = serializedObject.FindProperty("LootPickUpClip");
        m_SpawnEventProp = serializedObject.FindProperty("Events");

        m_FoldoutInfos = new bool[m_SpawnEventProp.arraySize];

        Undo.undoRedoPerformed += RecomputeFoldout;
    }

    void OnDisable()
    {
        Undo.undoRedoPerformed -= RecomputeFoldout;
    }

    void RecomputeFoldout()
    {
        serializedObject.Update();

        var newFoldout = new bool[m_SpawnEventProp.arraySize];
        Array.Copy(m_FoldoutInfos, newFoldout, Mathf.Min(m_FoldoutInfos.Length, newFoldout.Length));
        m_FoldoutInfos = newFoldout;
    }

    public override void OnInspectorGUI()
    {
        serializedObject.Update();

        EditorGUILayout.PropertyField(m_PlayerProp);
        EditorGUILayout.PropertyField(m_SpawnSoundProp);
        EditorGUILayout.PropertyField(m_PickUpSoundProp);

        for (int i = 0; i < m_SpawnEventProp.arraySize; ++i)
        {
            var i1 = i;
            m_FoldoutInfos[i] = EditorGUILayout.BeginFoldoutHeaderGroup(m_FoldoutInfos[i], $"Slot {i}", null, (rect) => { ShowHeaderContextMenu(rect, i1); });

            if (m_FoldoutInfos[i])
            {
                var entriesArrayProp = m_SpawnEventProp.GetArrayElementAtIndex(i).FindPropertyRelative("Entries");

                int localToDelete = -1;

                EditorGUILayout.BeginHorizontal();
                GUILayout.Label("Item");
                GUILayout.Label("Weight");
                GUILayout.Space(16);
                EditorGUILayout.EndHorizontal();

                for (int j = 0; j < entriesArrayProp.arraySize; ++j)
                {
                    var entryProp = entriesArrayProp.GetArrayElementAtIndex(j);

                    var itemProp = entryProp.FindPropertyRelative("Item");
                    var weightProp = entryProp.FindPropertyRelative("Weight");

                    EditorGUILayout.BeginHorizontal();
                    EditorGUILayout.PropertyField(itemProp, GUIContent.none);
                    EditorGUILayout.PropertyField(weightProp, GUIContent.none);
                    if (GUILayout.Button("-", GUILayout.Width(16)))
                    {
                        localToDelete = j;
                    }
                    EditorGUILayout.EndHorizontal();
                }

                if (localToDelete != -1)
                {
                    entriesArrayProp.DeleteArrayElementAtIndex(localToDelete);
                }

                if (GUILayout.Button("Add New Entry", GUILayout.Width(100)))
                {
                    entriesArrayProp.InsertArrayElementAtIndex(entriesArrayProp.arraySize);
                }
            }

            EditorGUILayout.EndFoldoutHeaderGroup();
        }

        if (toDelete != -1)
        {
            m_SpawnEventProp.DeleteArrayElementAtIndex(toDelete);
            ArrayUtility.RemoveAt(ref m_FoldoutInfos, toDelete);
            toDelete = -1;
        }

        if (GUILayout.Button("Add new Slot"))
        {
            m_SpawnEventProp.InsertArrayElementAtIndex(m_SpawnEventProp.arraySize);
            serializedObject.ApplyModifiedProperties();

            //insert will copy the last element, which can lead to having to empty a large spawn event to start new
            //so we manually "empty" the new event
            var newElem = m_SpawnEventProp.GetArrayElementAtIndex(m_SpawnEventProp.arraySize - 1);
            var entries = newElem.FindPropertyRelative("Entries");

            entries.ClearArray();

            ArrayUtility.Add(ref m_FoldoutInfos, false);
        }

        serializedObject.ApplyModifiedProperties();
    }

    void ShowHeaderContextMenu(Rect position, int index)
    {
        var menu = new GenericMenu();
        menu.AddItem(new GUIContent("Remove"), false, () => { toDelete = index; });
        menu.DropDown(position);
    }
}
#endif