using System;

/// <summary>
/// A system to save purchase data
/// Works for consumables and non consumables
/// </summary
[Serializable]
public class ShopSaveData
{
    public string id;
    public LootItem item;

    public ShopSaveData(string id, LootItem item)
    {
        this.id = id;
        this.item = item;
    }
}
