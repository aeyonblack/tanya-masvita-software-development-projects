using UnityEngine;
using UnityEngine.UI;
using UnityEngine.EventSystems;

public class HotBarEntryUI : MonoBehaviour, IPointerClickHandler
{
    /// <summary>
    /// Item image
    /// </summary>
    public Image ItemImage;

    /// <summary>
    /// Hotbar index for this item
    /// </summary>
    public int HotbarEntry { get; set; } = -1;

    public HotbarUI Owner { get; set; }

    private int index;

    public void OnPointerClick(PointerEventData eventData)
    {
        //Owner.ObjectClicked(this);
        Owner.ItemClicked(this);
    }

    /// <summary>
    /// Weapon Entry is updated only when character data is initialized (only once during gameplay)
    /// Weapons occupy the hotbar in a predetermined order
    /// </summary>
    public void InitializeWeaponEntry()
    {
        Debug.Log("Hotbar Entry = " + HotbarEntry);
        var weapon = Owner.Character.StartingWeapons[HotbarEntry];
        bool isEnabled = weapon != null;

        gameObject.SetActive(isEnabled);

        if (isEnabled)
        {
            ItemImage.sprite = weapon.itemSprite;
        }
    }

    public void UpdateItemEntry()
    {
        var entry = Owner.Character.Inventory.Entries[HotbarEntry];
        bool isEnabled = entry != null;

        Debug.Log("isEnabled is " + isEnabled);

        gameObject.SetActive(isEnabled);

        if (isEnabled)
        {
            ItemImage.sprite = entry.Item.itemSprite;
        }
    }
}
