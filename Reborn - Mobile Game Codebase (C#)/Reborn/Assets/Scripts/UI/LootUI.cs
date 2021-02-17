using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

/// <summary>
/// Selectively displays names of individual loot items on screen
/// </summary>
public class LootUI : MonoBehaviour
{
    public static LootUI Instance { get; private set; }

    private const int BUTTON_OFFSET = 32;

    private struct ButtonText
    {
        public Button LootButton;
        public Text LootName;
    }

    private struct DisplayedLoot
    {
        public Loot TargetLoot;
        public ButtonText TargetButton;
    }

    public Button ButtonPrefab;

    private Queue<ButtonText> buttonQueue = new Queue<ButtonText>();
    private List<Loot> offScreenLoot = new List<Loot>();
    private List<DisplayedLoot> onScreenLoot = new List<DisplayedLoot>();

    private void OnEnable()
    {
        Instance = this;
    }

    private void Start()
    {
        for (int i = 0; i < 16; ++i)
        {
            Button b = Instantiate(ButtonPrefab, transform);
            b.gameObject.SetActive(false);
            Text t = b.GetComponentInChildren<Text>();
            buttonQueue.Enqueue(new ButtonText() { LootButton = b, LootName = t });
        }
    }

    public void NewLoot(Loot loot)
    {
        Vector3 screenPos;
        if (OnScreen(loot.transform.position, out screenPos))
        {
            AddButton(loot, screenPos);
        }
        else
        {
            offScreenLoot.Add(loot);
        }
    }

    private void AddButton(Loot l, Vector3 screenPosition)
    {
        DisplayedLoot dl;

        dl.TargetLoot = l;
        dl.TargetButton = buttonQueue.Dequeue();
        dl.TargetButton.LootButton.gameObject.SetActive(true);
        dl.TargetButton.LootButton.transform.position = screenPosition + Vector3.up * BUTTON_OFFSET;

        dl.TargetButton.LootButton.onClick.RemoveAllListeners();

        dl.TargetButton.LootName.text = l.Item.itemName;

        onScreenLoot.Add(dl);
    }

    private bool OnScreen(Vector3 position, out Vector3 screenPosition)
    {
        screenPosition = Camera.main.WorldToScreenPoint(position);
        return (screenPosition.x >= 0 && screenPosition.y >= 0 && screenPosition.x <= Screen.width && screenPosition.y <= Screen.height);
    }

    // Update is called once per frame
    private void Update()
    {
        List<Loot> newOffscreen = new List<Loot>();

        for (int i = 0; i < onScreenLoot.Count; ++i)
        {
            Vector3 sp;
            var entry = onScreenLoot[i];

            if (entry.TargetLoot != null && OnScreen(entry.TargetLoot.transform.position, out sp))
            {
                entry.TargetButton.LootButton.transform.position = sp + Vector3.up * BUTTON_OFFSET;
            }
            else
            {
                onScreenLoot.RemoveAt(i);
                entry.TargetButton.LootButton.gameObject.SetActive(false);
                buttonQueue.Enqueue(entry.TargetButton);
                newOffscreen.Add(entry.TargetLoot);
                i--;
            }
        }

        for (int i = 0; i < offScreenLoot.Count; ++i)
        {
            Vector3 sp;
            var loot = offScreenLoot[i];

            if (loot != null)
            {
                if (OnScreen(loot.transform.position, out sp))
                {
                    AddButton(loot, sp);
                    offScreenLoot.RemoveAt(i);
                    i--;
                }
            }
            else
            {
                offScreenLoot.RemoveAt(i);
                i--;
            }
        }

        //do that at the end so we don't recompute their position in the second loop
        offScreenLoot.AddRange(newOffscreen);
    }
}

