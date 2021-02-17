using System;

/// <summary>
/// A system to save weapon purchase data
/// </summary>
[Serializable]
public class WeaponShopSaveData
{
    public string id;
    public LootItem weapon;

    public WeaponShopSaveData(string id, LootItem weapon)
    {
        this.id = id;
        this.weapon = weapon;
    }
}
