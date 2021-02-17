using System.Collections.Generic;
using UnityEngine;

/// <summary>
/// The game data store
/// </summary>
public sealed class GameDataStore : GameDataStoreBase
{
    /// <summary>
    /// Holds basic game stats 
    /// </summary>
    public GameSaveData GameData = new GameSaveData();

    /// <summary>
    /// List of completed levels
    /// </summary>
    public List<LevelSaveData> completedLevels = new List<LevelSaveData>();

    /// <summary>
    /// List of purchased weapons
    /// </summary>
    public List<WeaponShopSaveData> purchasedWeapons = new List<WeaponShopSaveData>();

    /// <summary>
    /// List of purchased items
    /// </summary>
    public List<ShopSaveData> purchasedItems = new List<ShopSaveData>();

    /// <summary>
    /// Set to true when player purchases the remove ads package
    /// </summary>
    public bool removeAds = false;


    public override void PreSave()
    {
        Debug.Log("[GAME] Saving Game...");
    }

    public override void PostLoad()
    {
        Debug.Log("[GAME] Game Loaded");
    }

    #region Game Data

    public void UpdateCurrentLevel(int level)
    {
        GameData.UpdateCurrentLevel(level);
    }

    public void UpdateXp(int amount)
    {
        GameData.UpdateXp(amount);
    }

    public void UpdateScore(int amount)
    {
        GameData.UpdateScore(amount);
    }

    public void UpdateTime(float time)
    {
        GameData.UpdateTime(time);
    }

    public void UpdateGold(int amount)
    {
        GameData.UpdateGold(amount);
    }

    public void UpdateGems(int amount)
    {
        GameData.UpdateGems(amount);
    }

    public void UpdateEyrieStones(int amount)
    {
        GameData.UpdateEyrieStones(amount);
    }

    public void UpdateDamageDone(float damage)
    {
        GameData.UpdateDamageDone(damage);
    }

    public void UpdateDamageTaken(float damage)
    {
        GameData.UpdateDamageTaken(damage);
    }

    public void UpdateTotalHits(float hits)
    {
        GameData.UpdateTotalHits(hits);
    }

    public void UpdateEnemiesKilled(int amount)
    {
        GameData.UpdateEnemiesKilled(amount);
    }

    public void UpdateXpToNextLevel(int amount)
    {
        GameData.UpdateXPToNextLevel(amount);
    }

    #endregion

    #region Save Functions

    /// <summary>
    /// Adds a level with id = levelId to list of completed levels and updates game data
    /// </summary>
    /// <param name="levelId">id of the completed level</param>
    /// <param name="xpEarned">amount of xp earned</param>
    /// <param name="goldEarned">gold earned by the player</param>
    /// <param name="gemsEarned">gems earned by the player</param>
    /// <param name="stonesCollected">stones collected during gameplay</param>
    /// <param name="time">time spent playing level</param>
    /// <param name="damageDone">damage done by player</param>
    /// <param name="damageTaken">damage taken by player from enemies</param>
    /// <param name="kills">total number of enemies killed by player</param>
    /// <param name="hits">total hits done during game session</param>
    /// <param name="playerLevel">the level the player is currently at</param>
    public bool CompleteLevel(string levelId, LevelList levelList, int xpEarned, int goldEarned, int gemsEarned,
        int stonesCollected, float time, float damageDone, float damageTaken, 
        int kills, float hits, int playerLevel, int xpToNext, float shotAccuracy, int score)
    {
        foreach (LevelSaveData level in completedLevels)
        {
            if (level.id == levelId)
            {
                Debug.Log("[Completing level....1]");
                level.time = Mathf.Min(level.time, time);
                return false;
            }
        }
        Debug.Log("[Completing level.....2]woaaah!!!");
        completedLevels.Add(new LevelSaveData(levelId, xpEarned, goldEarned, gemsEarned, 
            kills, damageDone, damageTaken, time, stonesCollected, hits, shotAccuracy, score));

        LevelItem completedLevel = levelList[levelId];

        if (completedLevel != null)
        {
            int index = levelList.IndexOf(completedLevel);
            int next = index + 1;
            if (levelList[next] != null)
            {
                // PROBLEM = the locked state of a level is not saved
                // FIGURE OUT WHY AND FIX IT
                levelList[next].Locked = false;
            }
        }

        return true;
    }

    public float GetTimeForLevel(string id)
    {
        foreach (LevelSaveData level in completedLevels)
        {
            if (id == level.id)
            {
                return level.time;
            }
        }
        return 0;
    }

    /// <summary>
    /// Adds a weapon to the list of weapons purchased/owned by the player
    /// </summary>
    /// <param name="id">Id of the weapon to purchase</param>
    public void PurchaseWeapon(string id, LootItem weapon)
    {
        purchasedWeapons.Add(new WeaponShopSaveData(id, weapon));
    }

    /// <summary>
    /// Upgrade the weapon to next level
    /// </summary>
    /// <param name="id"></param>
    /// <param name="item"></param>
    public void UpgradeWeapon(LootItem item)
    {
        WeaponConfiguration weapon = item as WeaponConfiguration;
        weapon.currentLevel += 1;
        int l = weapon.currentLevel;
        weapon.MaxAmmoCapacity = weapon.levels[l - 1].maxAmmoCapacity;
        weapon.fireRate = weapon.levels[l - 1].fireRate;
        weapon.damage = weapon.levels[l - 1].damage;
        weapon.Clip.size = weapon.levels[l - 1].clipSize;
    }
    
    /// <summary>
    /// Determines if a specific weapon has already been purchased
    /// </summary>
    /// <param name="id"></param>
    /// <returns></returns>
    public bool IsWeaponPurchased(string id)
    {
        foreach (WeaponShopSaveData weapon in purchasedWeapons)
        {
            if (weapon.id == id)
            {
                return true;
            }
        }
        return false;
    }

    public void PurchaseItem(string id, LootItem item)
    {
        purchasedItems.Add(new ShopSaveData(id, item));
    }

    /// <summary>
    /// Removes the item with specified id
    /// </summary>
    /// <param name="id">id to remove from list</param>
    public void RemoveItem(string id)
    {
        for (int i = 0; i < purchasedItems.Count; i++)
        {
            if (purchasedItems[i].id == id)
            {
                Debug.Log("Removing item... " + purchasedItems[i].item.itemName);
                purchasedItems.RemoveAt(i);
            }
        }
    }

    #endregion
}
