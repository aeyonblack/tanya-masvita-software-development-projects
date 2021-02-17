using UnityEngine;

/// <summary>
/// Used to classify items into their respective categories
/// </summary>
public enum ItemType
{
    Weapon,
    Ammo,
    Health,
    Armor,
    Booster,
    Gem,
    Gold
}

/// <summary>
/// Defines the currency used to purchase an item
/// </summary>
public enum CurrencyType
{
    Gems,
    Gold,
    Hard
}

/// <summary>
/// Base class for all lootable items, There are currently only two such items,
/// Weapons and Ammo. More items will be available as the project grows
/// </summary>
[CreateAssetMenu(fileName = "New Item", menuName = "Reborn/Loot", order = 3)]
public abstract class LootItem : ScriptableObject
{
    /// <summary>
    /// Item id;
    /// </summary>
    public string id;

    /// <summary>
    /// Name of this loot item 
    /// </summary>
    public string itemName;

    /// <summary>
    /// Item icon
    /// </summary>
    public Sprite itemSprite;

    /// <summary>
    /// Sprite used in shop
    /// </summary>
    public Sprite itemShopPoster;

    /// <summary>
    /// Currency icon
    /// </summary>
    public Sprite currencySprite;

    /// <summary>
    /// World prefab of the respective item
    /// For weapons, this is the weapon model without the fps arms
    /// </summary>
    public GameObject worldObjectPrefab;

    /// <summary>
    /// Summary of item's characteristics
    /// </summary>
    [Multiline]
    public string itemDescription;

    public bool isLocked = true;

    /// <summary>
    /// Required level to unlock this item
    /// </summary>
    public int requiredLevel;

    /// <summary>
    /// Cost to purchase this item
    /// </summary>
    public float itemCost;

    /// <summary>
    /// Points awarded to the player for collecting this loot
    /// </summary>
    public int pickUpPoints;

    /// <summary>
    /// If stackable, new entries of the item will be stacked ontop of each other 
    /// in the same slot.
    /// Weapons and ammo by default are not stackable
    /// But ammo might be stackable I think....
    /// </summary>
    public bool stackable;

    /// <summary>
    /// Type of this item
    /// </summary>
    public ItemType itemType;

    /// <summary>
    /// The currency used to purchase this item
    /// </summary>
    public CurrencyType currency;

    /// <summary>
    /// Played when item is used
    /// </summary>
    public AudioClip UsedSound;

    /// <summary>
    /// Invoked by inventory system when item is used
    /// </summary>
    /// <param name="user">Character data that used this item</param>
    /// <returns>Returns whether the item has been used so that it can be removed from the inventory</returns>
    public virtual bool UsedBy(CharacterData user)
    {
        return false;
    }

    /// <summary>
    /// Method used by the store to purchase this item
    /// </summary>
    /// <param name="user"></param>
    public virtual bool TryPurchaseItem()
    {
        return true; 
    }

    public virtual bool UpgradeItem()
    {
        return false;
    }

    public virtual string GetDescription()
    {
        return itemDescription;
    }

    /// <summary>
    /// Plays a sound to indicate item used
    /// </summary>
    protected virtual void PlayUsedSoundFX()
    {
        Controller.instance.AudioSource.PlayOneShot(UsedSound);
    }

}
