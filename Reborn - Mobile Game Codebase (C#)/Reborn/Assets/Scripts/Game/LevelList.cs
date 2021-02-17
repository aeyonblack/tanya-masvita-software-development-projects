﻿using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using UnityEngine;

/// <summary>
/// Level configuration data 
/// </summary>
/// 
[CreateAssetMenu(fileName = "LevelList", menuName = "Reborn/New Level List")]
public class LevelList : ScriptableObject, IList<LevelItem>, IDictionary<string, LevelItem>, ISerializationCallbackReceiver
{
    public LevelItem[] levels;

    /// <summary>
    /// Cached dictionary of levels by their ids
    /// </summary>
    private IDictionary<string, LevelItem> m_LevelDictionary;

    /// <summary>
    /// Returns the number of levels
    /// </summary>
    public int Count
    {
        get { return levels.Length; }
    }

    /// <summary>
    /// Level list is always read-only
    /// </summary>
    public bool IsReadOnly
    {
        get { return true; }
    }

    /// <summary>
    /// Gets a level by index
    /// </summary>
    /// <param name="i"></param>
    /// <returns></returns>
    public LevelItem this[int i]
    {
        get { return levels[i]; }
    }

    /// <summary>
    /// Gets a level by id
    /// </summary>
    /// <param name="key"></param>
    /// <returns></returns>
    public LevelItem this[string key]
    {
        get { return m_LevelDictionary[key]; }
    }

    /// <summary>
    /// Gets a collection of all level keys
    /// </summary>
    public ICollection<string> Keys
    {
        get { return m_LevelDictionary.Keys; }
    }

    /// <summary>
    /// Gets the index of a given level
    /// </summary>
    /// <param name="levelItem"></param>
    /// <returns></returns>
    public int IndexOf(LevelItem item)
    {

        if (item == null)
        {
            return -1;
        }

        for (int i = 0; i < levels.Length; ++i)
        {
            if (levels[i] == item)
            {
                return i;
            }
        }

        return -1;

    }
     
    /// <summary>
    /// Returns whether this level exists in the list
    /// </summary>
    /// <param name="item"></param>
    /// <returns></returns>
    public bool Contains(LevelItem item)
    {
        return IndexOf(item) >= 0;
    }

    /// <summary>
    /// Gets whether a level of the given id exists
    /// </summary>
    /// <param name="item"></param>
    /// <returns></returns>
    public bool ContainsKey(string key)
    {
        return m_LevelDictionary.ContainsKey(key);
    }

    /// <summary>
    /// Try get a level with the given key sojkk'
    /// </summary>
    /// <param name="key"></param>
    /// <param name="value"></param>
    /// <returns></returns>
    public bool TryGetValue(string key, out LevelItem value)
    {
        return m_LevelDictionary.TryGetValue(key, out value);
    }

    /// <summary>
    /// Gets the <see cref="LevelItem"/> Associated with the scene
    /// </summary>
    /// <param name="scene"></param>
    /// <returns></returns>
    public LevelItem GetLevelByScene(string scene)
    {
        for (int i = 0; i < levels.Length; ++i)
        {
            LevelItem item = levels[i];
            if (item != null && item.sceneName == scene)
            {
                return item;
            }
        }
        return null;
    }

    void ISerializationCallbackReceiver.OnBeforeSerialize()
    {
    }

    void ISerializationCallbackReceiver.OnAfterDeserialize()
    {
        m_LevelDictionary = levels.ToDictionary(l => l.id);
    }

    ICollection<LevelItem> IDictionary<string, LevelItem>.Values
    {
        get { return m_LevelDictionary.Values; }
    }

    LevelItem IList<LevelItem>.this[int i]
    {
        get { return levels[i]; }
        set { throw new NotSupportedException("Level List is read only"); }
    }

    LevelItem IDictionary<string, LevelItem>.this[string key]
    {
        get { return m_LevelDictionary[key]; }
        set { throw new NotSupportedException("Level List is read only"); }
    }

    void IList<LevelItem>.Insert(int index, LevelItem item)
    {
        throw new NotSupportedException("Level List is read only");
    }

    void IList<LevelItem>.RemoveAt(int index)
    {
        throw new NotSupportedException("Level List is read only");
    }

    void ICollection<LevelItem>.Add(LevelItem item)
    {
        throw new NotSupportedException("Level List is read only");
    }

    void ICollection<KeyValuePair<string, LevelItem>>.Add(KeyValuePair<string, LevelItem> item)
    {
        throw new NotSupportedException("Level List is read only");
    }

    void ICollection<KeyValuePair<string, LevelItem>>.Clear()
    {
        throw new NotSupportedException("Level List is read only");
    }

    bool ICollection<KeyValuePair<string, LevelItem>>.Contains(KeyValuePair<string, LevelItem> item)
    {
        return m_LevelDictionary.Contains(item);
    }

    void ICollection<KeyValuePair<string, LevelItem>>.CopyTo(KeyValuePair<string, LevelItem>[] array, int arrayIndex)
    {
        m_LevelDictionary.CopyTo(array, arrayIndex);
    }

    void ICollection<LevelItem>.Clear()
    {
        throw new NotSupportedException("Level List is read only");
    }

    void ICollection<LevelItem>.CopyTo(LevelItem[] array, int arrayIndex)
    {
        levels.CopyTo(array, arrayIndex);
    }

    bool ICollection<LevelItem>.Remove(LevelItem item)
    {
        throw new NotSupportedException("Level List is read only");
    }

    public IEnumerator<LevelItem> GetEnumerator()
    {
        return ((IList<LevelItem>)levels).GetEnumerator();
    }

    IEnumerator<KeyValuePair<string, LevelItem>> IEnumerable<KeyValuePair<string, LevelItem>>.GetEnumerator()
    {
        return m_LevelDictionary.GetEnumerator();
    }

    IEnumerator IEnumerable.GetEnumerator()
    {
        return levels.GetEnumerator();
    }

    void IDictionary<string, LevelItem>.Add(string key, LevelItem value)
    {
        throw new NotSupportedException("Level List is read only");
    }

    bool ICollection<KeyValuePair<string, LevelItem>>.Remove(KeyValuePair<string, LevelItem> item)
    {
        throw new NotSupportedException("Level List is read only");
    }

    bool IDictionary<string, LevelItem>.Remove(string key)
    {
        throw new NotSupportedException("Level List is read only");
    }
}
