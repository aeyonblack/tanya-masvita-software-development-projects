using UnityEngine;
using UnityEngine.UI;

/// <summary>
/// Handles the inventory window
/// </summary>
public class InventoryUI : MonoBehaviour
{
    /// <summary>
    /// Reference to the player
    /// </summary>
    public CharacterData Character
    {
        get { return data; }
    }


    /// <summary>
    /// Inventory Slots
    /// </summary>
    public RectTransform[] ItemSlots;

    /// <summary>
    /// Inventory Item Entry
    /// </summary>
    public ItemEntryUI ItemEntryPrefab;

    /// <summary>
    /// Inventory item tool tip 
    /// Provides a basic description of the selected inventory item 
    /// and some basic actions e.g. Use item or Equip weapon
    /// </summary>
    public ItemToolTip Tooltip;

    /// <summary>
    /// Private reference to the character data 
    /// </summary>
    private CharacterData data;

    /// <summary>
    /// Inventory items
    /// </summary>
    private ItemEntryUI[] itemEntries;

    public void Init()
    {
        itemEntries = new ItemEntryUI[ItemSlots.Length];

        for (int i = 0; i < itemEntries.Length; ++i)
        {
            itemEntries[i] = Instantiate(ItemEntryPrefab, ItemSlots[i]);
            itemEntries[i].gameObject.SetActive(false);
            itemEntries[i].Owner = this;
            itemEntries[i].InventoryEntry = i;
            itemEntries[i].IconeImage.preserveAspect = true;
        }

        Tooltip.Owner = this;
    }

    private void OnEnable()
    {
        Tooltip.gameObject.SetActive(false);
    }

    public void Load(CharacterData data)
    {
        this.data = data;
        for (int i = 0; i < itemEntries.Length; ++i)
        {
            itemEntries[i].UpdateEntry();
        }
    }

    public void ObjectClicked(ItemEntryUI item)
    {
        LootItem selectedItem = data.Inventory.Entries[item.InventoryEntry].Item;
        Tooltip.SelectedItem = data.Inventory.Entries[item.InventoryEntry];

        Tooltip.ItemImage.preserveAspect = true;
        Tooltip.gameObject.SetActive(true);
        Tooltip.ItemImage.sprite = selectedItem.itemSprite;
        Tooltip.ItemName.text = selectedItem.itemName;
        Tooltip.ItemDescription.text = selectedItem.itemDescription;

        if (selectedItem.itemType == ItemType.Weapon)
        {
            if (selectedItem != data.CurrentWeapon.weaponConfiguration)
            {
                Tooltip.UseItemButton.gameObject.SetActive(true);
                Tooltip.WeaponEquipedText.gameObject.SetActive(false);
                Tooltip.UseItemButton.interactable = false;
            }
            else
            {
                Tooltip.UseItemButton.gameObject.SetActive(false);
                Tooltip.WeaponEquipedText.gameObject.SetActive(true);
            }
        }
        else
        {
            Tooltip.UseItemButton.interactable = true;
            Tooltip.UseItemButton.gameObject.SetActive(true);
            Tooltip.WeaponEquipedText.gameObject.SetActive(false);
            Tooltip.ButtonText.text = "Use";
        }
    }

    public void ObjectClicked(Inventory.InventoryEntry item)
    {
        var selectedItem = item.Item;

        if (selectedItem.itemType == ItemType.Weapon)
        {
            if (selectedItem != data.CurrentWeapon.weaponConfiguration)
            {
                Tooltip.UseItemButton.gameObject.SetActive(true);
                Tooltip.WeaponEquipedText.gameObject.SetActive(false);
                Tooltip.ButtonText.text = "Equip";
            }
            else
            {
                Tooltip.UseItemButton.gameObject.SetActive(false);
                Tooltip.WeaponEquipedText.gameObject.SetActive(true);
            }
        }
        else
        {
            Tooltip.UseItemButton.gameObject.SetActive(true);
            Tooltip.WeaponEquipedText.gameObject.SetActive(false);
            Tooltip.ButtonText.text = "Use";
        }
    }
}
