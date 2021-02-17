using System.Collections;
using System.Collections.Generic;
using UnityEngine;

/// <summary>
/// Handles the player's inventory. The inventory will be simple and elegant with a circular shape.
/// There will be atmost 16 slots with each containing only one type of item. Weapons and ammo
/// are the only inventory items that cannot be stacked. 
/// </summary>
public class Inventory
{
    /// <summary>
    /// Defines one entry in the inventory
    /// Holds the type of item and the quantity in that slot
    /// </summary>
    public class InventoryEntry
    {
        /// <summary>
        /// The item in this entry
        /// </summary>
        public LootItem Item;

        /// <summary>
        /// Number of items stored here (Always one for non-stackable items)
        /// </summary>
        public int Count;

        /// <summary>
        /// Inventory slot of this entry
        /// </summary>
        public int Slot;
    }

    /// <summary>
    /// 30 Entries in the inventory
    /// </summary>
    public InventoryEntry[] Entries = new InventoryEntry[30];

    /// <summary>
    /// Remaining space in the inventory
    /// </summary>
    public int FreeSpace { get; set; }

    /// <summary>
    /// Remaining space for weapons
    /// </summary>
    public int WeaponSpace { get; set; }

    /// <summary>
    /// The Character data for this inventory
    /// </summary>
    CharacterData m_Player;

    /// <summary>
    /// Initialize the inventory
    /// </summary>
    /// <param name="player">Character data that initialized this inventory</param>
    public void Initialize(CharacterData player)
    {
        FreeSpace = player.Stats.currentEncumbrance;
        WeaponSpace = player.Stats.currentWeaponEncumbrance;
        m_Player = player;
    }

    /// <summary>
    /// Add an item to the inventory.
    /// Check if the item already exists in the inventory
    /// If the item exists and is not stackable use the item
    /// immediately, if the item is a weapon fill up the clip
    /// else increment the count in the slot (= 1 if item was not in the inventory)
    /// </summary>
    /// <param name="item"></param>
    public void AddItem(LootItem item)
    {
        // Maximum amount of items that can be carried has been reached
        // We cannot add this item
        if (FreeSpace <= 0 && item.itemType != ItemType.Weapon)
        {
            Debug.Log("The maximum number of items you can carry has been reached");
            return;
        }

        bool found = false;
        int firstEmpty = -1;

        for(int i = 0; i < 16; i++)
        {
            if (Entries[i] == null)
            {
                if (firstEmpty == -1)
                    firstEmpty = i;
            }
            else if (Entries[i].Item == item)
            {
                if (Entries[i].Item.stackable)
                    Entries[i].Count += 1;
                else
                {  
                    if (!item.UsedBy(m_Player))
                    {
                        // This is ammo, increase the count
                        Debug.Log("[Inventory] This item is not stackable but could not be used, stacking the item anyway");
                        Entries[i].Count += 1;
                        FreeSpace -= 1;
                    }
                }
                m_Player.Experience.AddExperience(item.pickUpPoints);
                found = true;
            }  
        }

        // A new item entry is done here because no such item has been found in the inventory
        if (!found && firstEmpty != -1)
        {
            if (!item.stackable)
            {
                if (item.itemType != ItemType.Weapon)
                {
                    // If this is not a weapon, try to use it before storing it
                    if (!item.UsedBy(m_Player))
                    {
                        InventoryEntry newEntry = new InventoryEntry();
                        newEntry.Item = item;
                        newEntry.Count = 1;
                        Entries[firstEmpty] = newEntry;
                        newEntry.Slot = firstEmpty;

                        FreeSpace -= 1;

                        m_Player.Experience.AddExperience(item.pickUpPoints);
                        m_Player.UpdateEncumbranceUI();
                    }
                }
                else
                {
                    // This is a weapon, just store it
                    InventoryEntry newEntry = new InventoryEntry();
                    newEntry.Item = item;
                    newEntry.Count = 1;
                    Entries[firstEmpty] = newEntry;
                    newEntry.Slot = firstEmpty;

                    WeaponSpace -= 1;
                    m_Player.UpdateEncumbranceUI();
                }
            }
            else
            {
                InventoryEntry entry = new InventoryEntry();
                entry.Item = item;
                entry.Count = 1;

                Entries[firstEmpty] = entry;
                entry.Slot = firstEmpty;
                FreeSpace -= 1;

                m_Player.Experience.AddExperience(item.pickUpPoints);
                m_Player.UpdateEncumbranceUI();
            }
            Debug.Log("[Inventory] Done Adding");
        }
    }

    /// <summary>
    /// Try to use the item
    /// If the item is a weapon then it is equiped
    /// Otherwise the item is used accordingly and its stack count decreases
    /// </summary>
    /// <param name="item">The item to be used</param>
    /// <returns>Returns true if the item is used successfully</returns>
    public bool UseItem(InventoryEntry entry)
    {
        if (entry.Item.UsedBy(m_Player))
        {
            if (entry.Item.stackable || entry.Item.itemType == ItemType.Ammo)
            {
                entry.Count -= 1;
                if (entry.Count <= 0)
                {
                    Entries[entry.Slot] = null;
                    FreeSpace += 1;
                }
                m_Player.HotbarWindow.UpdateItemEntry();
                m_Player.HotbarWindow.Load();
                m_Player.InventoryWindow.Load(m_Player);
            }
            GameManager.instance.RemoveItemFromList(entry.Item.id);
            return true;
        }
        return false;
    }

}
