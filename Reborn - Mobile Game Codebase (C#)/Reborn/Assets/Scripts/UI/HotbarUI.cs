using UnityEngine;
using UnityEngine.UI;

/// <summary>
/// Handles Hotbar UI display
/// Designed for better accessibility of important items
/// Weapons, Health Potions and Ammo
/// </summary>
public class HotbarUI : MonoBehaviour
{
    public CharacterData Character
    {
        get
        {
            return data;
        }
    }

    /// <summary>
    /// Hotbar slots holding different items 
    /// </summary>
    public RectTransform[] HotbarSlots;

    /// <summary>
    /// Hotbar item prefab
    /// </summary>
    public HotBarEntryUI HotbarEntryPrefab;

    /// <summary>
    /// Other weapon 
    /// </summary>
    public Image HotImg1;

    /// <summary>
    /// Other weapon
    /// </summary>
    public Image HotImg2;

    /// <summary>
    /// Pistol placed here
    /// </summary>
    public Image HotImg3;

    /// <summary>
    /// Hotbars
    /// </summary>
    private HotBarEntryUI[] hotbarEntries;

    private CharacterData data;

    /// <summary>
    /// Initialize hotbar entries
    /// </summary>
    /// <param name="character"></param>
    public void Init(CharacterData character)
    {
        data = character;

        hotbarEntries = new HotBarEntryUI[HotbarSlots.Length];
        
        for (int i = 0; i < hotbarEntries.Length; ++i)
        {
            hotbarEntries[i] = Instantiate(HotbarEntryPrefab, HotbarSlots[i]);
            hotbarEntries[i].gameObject.SetActive(false);
            hotbarEntries[i].Owner = this;
            hotbarEntries[i].ItemImage.preserveAspect = true;
        }

        // Assign weapons to slots
        // My favourite algorithm in the whole codebase 
        for (int i = 0; i < data.StartingWeapons.Length; ++i)
        {
            if (data.StartingWeapons[i] != null)
            {
                if (data.StartingWeapons[i].Ammo.Type == Ammo.AmmoType.PISTOL)
                {
                    // If this weapon is a pistol add it to the pistol hotbar slot
                    AssignEntry(3, i);
                }
                else
                {
                    if (i+1 < 3)
                    {
                        // i + 1 is either 1 or 2
                        // i = 0 or 1
                        if (hotbarEntries[i+1].HotbarEntry == -1)
                        {
                            AssignEntry(i + 1, i);
                            ModifySize(i + 1, i);
                        }
                    }
                    else
                    {
                        // i + 1 = 3, i = 2
                        if (hotbarEntries[i].HotbarEntry == -1)
                        {
                            AssignEntry(i, i);
                            ModifySize(i, i);
                        }
                        else
                        {
                            AssignEntry(i - 1, i);
                            ModifySize(i - 1, i);
                        }
                    }
                }
            }
        }
    }

    private void AssignEntry(int index, int entry)
    {
        hotbarEntries[index].HotbarEntry = entry;
        WeaponConfiguration currentWeapon = Character.CurrentWeapon.weaponConfiguration;
        if (index == 1)
        {
            if (currentWeapon == Character.StartingWeapons[entry])
            {
                HotImg1.color = new Color32(255, 255, 0, 190);
                HotImg2.color = new Color32(0, 0, 0, 152);
                HotImg3.color = new Color32(0, 0, 0, 152);
            }
        }
        else if (index == 2)
        {
            if (currentWeapon == Character.StartingWeapons[entry])
            {
                HotImg1.color = new Color32(0, 0, 0, 152);
                HotImg2.color = new Color32(255, 255, 0, 190);
                HotImg3.color = new Color32(0, 0, 0, 152);
            }
        }
        else if (index == 3)
        {
            if (currentWeapon == Character.StartingWeapons[entry])
            {
                HotImg1.color = new Color32(0, 0, 0, 152);
                HotImg2.color = new Color32(0, 0, 0, 152);
                HotImg3.color = new Color32(255, 255, 0, 190);
            }
        }
        hotbarEntries[index].InitializeWeaponEntry();
    }

    /// <summary>
    /// Increase the size of the image for better visibility
    /// </summary>
    /// <param name="index"></param>
    /// <param name="i"></param>
    private void ModifySize(int index, int i)
    {
        if (data.StartingWeapons[i] == null)
        {
            return;
        }
        if (data.StartingWeapons[i].Ammo.Type != Ammo.AmmoType.SUBMACHINE)
        {
            Debug.Log("Weapon Ammo is " + data.StartingWeapons[i].Ammo.Type.ToString());
            Debug.Log("i = " + i + "index = " + index);
            hotbarEntries[index].ItemImage.rectTransform.sizeDelta = new Vector2(200, 200);
        }
    }

    /// <summary>
    /// Update hotbar entries
    /// </summary>
    public void UpdateItemEntry()
    {
        for (int i = 0; i < hotbarEntries.Length-1; i += 4)
        {
            if (hotbarEntries[i].HotbarEntry > -1)
            {
                hotbarEntries[i].UpdateItemEntry();
                Debug.Log("[Updating Item entry] Hotbar entry = " + hotbarEntries[i].HotbarEntry);
            }
        }
    }

    /// <summary>
    /// Set up health and booster slots
    /// </summary>
    public void Load()
    {
        // Setup Health and Ammo slots
        for (int i = 0; i < hotbarEntries.Length; i += 4)
        {
            // i  is either equal to 0 or 4
            for (int j = 0; j < data.Inventory.Entries.Length; ++j)
            {
                if (data.Inventory.Entries[j] != null && data.Inventory.Entries[j].Item.itemType == ItemType.Booster)
                {
                    if (i == 0 && hotbarEntries[i].HotbarEntry == -1)
                    {
                        hotbarEntries[i].HotbarEntry = j;
                        hotbarEntries[i].UpdateItemEntry();
                    }
                }
                else if (data.Inventory.Entries[j] != null && data.Inventory.Entries[j].Item.itemType == ItemType.Health)
                {
                    if (i == 4)
                    {
                        hotbarEntries[i].HotbarEntry = j;
                        hotbarEntries[i].UpdateItemEntry();
                    }
                }
            }
        }
    }

    /// <summary>
    /// Use the clicked item
    /// highlight the clicked hotbar
    /// </summary>
    /// <param name="item">Clicked item</param>
    public void ItemClicked(HotBarEntryUI item)
    {
        int temp = item.HotbarEntry;
        if (data.StartingWeapons[0] == null)
        {
            if (data.StartingWeapons[1] && data.StartingWeapons[2])
            {
                // 6
                if (item.HotbarEntry == 1)
                    item.HotbarEntry = 0;
                else
                    item.HotbarEntry = 1;
            }
            else if (data.StartingWeapons[1] && data.StartingWeapons[2] == null)
            {
                // 2
                item.HotbarEntry = 0;
            }
            else if (data.StartingWeapons[2] && data.StartingWeapons[1] == null)
            {
                // 3
                item.HotbarEntry = 0;
            }
        }
        else if (data.StartingWeapons[1] == null)
        {
            if (data.StartingWeapons[0] && data.StartingWeapons[2])
            {
                if (item.HotbarEntry == 2)
                    item.HotbarEntry = 1;
            }
        }

        var selectedItem = data.Inventory.Entries[item.HotbarEntry];
        data.InventoryWindow.ObjectClicked(selectedItem);
        data.Inventory.UseItem(selectedItem);

        if (item.HotbarEntry == hotbarEntries[1].HotbarEntry)
        {
            HotImg1.color = new Color32(255, 255, 0, 190);
            HotImg2.color = new Color32(0, 0, 0, 152);
            HotImg3.color = new Color32(0, 0, 0, 152);
        }
        else if (item.HotbarEntry == hotbarEntries[2].HotbarEntry)
        {
            HotImg1.color = new Color32(0, 0, 0, 152);
            HotImg2.color = new Color32(255, 255, 0, 190);
            HotImg3.color = new Color32(0, 0, 0, 152);
        }
        else if (item.HotbarEntry == hotbarEntries[3].HotbarEntry)
        {
            HotImg1.color = new Color32(0, 0, 0, 152);
            HotImg2.color = new Color32(0, 0, 0, 152);
            HotImg3.color = new Color32(255, 255, 0, 190);
        }

        item.HotbarEntry = temp;
    }

}
