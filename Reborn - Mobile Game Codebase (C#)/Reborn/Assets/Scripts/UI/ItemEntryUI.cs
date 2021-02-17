using UnityEngine;
using UnityEngine.UI;
using UnityEngine.EventSystems;

public class ItemEntryUI : MonoBehaviour, IPointerClickHandler
{
    public Image IconeImage;
    public Text ItemCount;

    public int InventoryEntry { get; set; } = -1;

    public InventoryUI Owner { get; set; }
    public int Index { get; set; }

    public void OnPointerClick(PointerEventData eventData)
    {
        Owner.ObjectClicked(this);
    }

    /// <summary>
    /// Update the inventory entry
    /// </summary>
    public void UpdateEntry()
    {
        var entry = Owner.Character.Inventory.Entries[InventoryEntry];
        bool isEnabled = entry != null;

        gameObject.SetActive(isEnabled);

        if (isEnabled)
        {
            IconeImage.sprite = entry.Item.itemSprite;

            if (entry.Count > 1)
            {
                ItemCount.gameObject.SetActive(true);
                ItemCount.text = entry.Count.ToString();
            }
            else
            {
                ItemCount.gameObject.SetActive(false);
            }
        }
        else
        {
            if (Owner.Tooltip.gameObject.activeSelf)
            {
                Owner.Tooltip.gameObject.SetActive(false);
            }
        }
    }
}
