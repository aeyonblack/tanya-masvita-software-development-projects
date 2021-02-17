using System.Collections.Generic;
using UnityEngine; 
using UnityEngine.SceneManagement;
using UnityEngine.UDP;

/// <summary>
/// Persistent Singleton that handles game states and data
/// All gameloops are handled here 
/// </summary>
public class GameManager : GameManagerBase<GameManager, GameDataStore>
{
    /// <summary>
    /// List of weapons the player starts with at level start
    /// </summary>
    [HideInInspector]
    public WeaponConfiguration[] StartingWeapons = new WeaponConfiguration[3];

    /// <summary>
    /// Reference to the list of all purchased items
    /// </summary>
    [HideInInspector]
    public List<ShopSaveData> PurchasedItems;

    /// <summary>
    /// Scriptable object for the list of levels
    /// </summary>
    public LevelList LevelList;

    /// <summary>
    /// List of all weapons the player currently owns
    /// </summary>
    [HideInInspector]
    public List<WeaponShopSaveData> PurchasedWeapons;

    [Header("Default Weapons")]
    public string WeaponId1;
    public string WeaponId2;
    public LootItem Weapon1;
    public LootItem Weapon2;

    /// <summary>
    /// Gems currency
    /// </summary>
    public Gems Gems;

    /// <summary>
    /// Gold Currency
    /// </summary>
    public Gold Gold;

    //Testing game
    public bool TestMode;

    [Header("Social Media Urls")]
    public string InstagramUrl;
    public string TwitterUrl;
    public string YoutubeUrl;

    private IInitListener listener = new InitListener();

    /// <summary>
    /// Set sleep timeout to never sleep 
    /// </summary>
    protected override void Awake()
    {
        Screen.sleepTimeout = SleepTimeout.NeverSleep;
        base.Awake();

        Gems = new Gems(m_DataStore.GameData.gems);
        Gold = new Gold(m_DataStore.GameData.gold);

        Gems.currencyChanged += OnGemsChanged;
        Gold.currencyChanged += OnGoldChanged;
    }

    protected override void Start()
    {
        base.Start();

        // No need to purchase weapons
        AddDefaultWeapons(); 
        PurchasedWeapons = m_DataStore.purchasedWeapons;
        PurchasedItems = m_DataStore.purchasedItems;

        StoreService.Initialize(listener);
    }

    /// <summary>
    /// Prototype method for erasing, game data
    /// </summary>
    public void ClearData()
    {
        m_DataStore.GameData = new GameSaveData();

        ClearLevelData();
        m_DataStore.completedLevels = new List<LevelSaveData>();
        m_DataStore.purchasedItems = new List<ShopSaveData>();
        m_DataStore.purchasedWeapons = new List<WeaponShopSaveData>();
        m_DataStore.removeAds = false;
        SaveData();
    }

    /// <summary>
    /// Clears all level data and progress
    /// </summary>
    public void ClearLevelData()
    {
        for (int i = 1; i < LevelList.Count; i++)
        {
            LevelItem level = LevelList[i];
            level.Locked = true;
            level.time = 0;
        }
    }

    private void AddDefaultWeapons()
    {
        AddDefaultWeapon(WeaponId1, Weapon1);
        AddDefaultWeapon(WeaponId2, Weapon2);
    }

    private void AddDefaultWeapon(string id, LootItem weapon)
    {
        if (m_DataStore.IsWeaponPurchased(id))
            return;
        m_DataStore.PurchaseWeapon(id, weapon);
        SaveData();
    }

    /// <summary>
    /// Add level with id = levelId to list of completed levels
    /// Save all level data and update game data
    /// </summary>
    /// <param name="levelId">id of the level to be completed</param>
    /// <param name="xpEarned">xp earned in the level</param>
    /// <param name="goldEarned">gold earned in the level</param>
    /// <param name="gemsEarned">gems earned in the level</param>
    /// <param name="stonesCollected">stones collected in the level</param>
    /// <param name="time">time spent playing the level</param>
    /// <param name="damageDone">damage done by the player</param>
    /// <param name="damageTaken">damage done to the player</param>
    /// <param name="kills">number enemies killed by player</param>
    /// <param name="hits">total number of hits counted during game session</param>
    /// <param name="playerLevel">the current level the player has reached</param>
    public void CompleteLevel(string levelId, int xpEarned, int goldEarned, int gemsEarned, 
        int stonesCollected, float time, float damageDone, float damageTaken, int kills, 
        float hits, int playerLevel, int xpToNext, float shotAccuracy, int score)
    {

        if (!LevelList.ContainsKey(levelId))
        {
            return;
        }

        if (m_DataStore.CompleteLevel(levelId, LevelList, xpEarned, goldEarned, gemsEarned,
            stonesCollected, time, damageDone, damageTaken, kills, hits, playerLevel, xpToNext, shotAccuracy, score))
        {
            m_DataStore.UpdateXp(xpEarned);
            Gold.ChangeCurrency(goldEarned);
            Gems.ChangeCurrency(gemsEarned);
            m_DataStore.UpdateEyrieStones(stonesCollected);
            m_DataStore.UpdateTime(time);
            m_DataStore.UpdateDamageDone(damageDone);
            m_DataStore.UpdateDamageTaken(damageTaken);
            m_DataStore.UpdateEnemiesKilled(kills);
            m_DataStore.UpdateTotalHits(hits);
            m_DataStore.UpdateCurrentLevel(playerLevel);
            m_DataStore.UpdateXpToNextLevel(xpToNext);
            m_DataStore.UpdateScore(score);
        }
        Debug.Log("[CompleteLevel] Saving Data");
        SaveData();
    }

    /// <summary>
    /// Gets the id for the current scene
    /// and returns a level item to represent that scene
    /// </summary>
    /// <returns>Level Id</returns>
    public LevelItem GetLevelForCurrentScene()
    {
        string sceneName = SceneManager.GetActiveScene().name;
        return LevelList.GetLevelByScene(sceneName);
    }

    /// <summary>
    /// Determines if a specified level is completed
    /// </summary>
    /// <param name="levelId">Id for the level to check</param>
    /// <returns></returns>
    public bool IsLevelCompleted(string levelId)
    {
        return false;
    }

    /// <summary>
    /// Get time to complete level with levelId = id
    /// </summary>
    /// <param name="id">id for the level</param>
    /// <returns>time in seconds</returns>
    public float GetTimeForLevel(string id)
    {
        if (!LevelList.ContainsKey(id))
        {
            Debug.LogWarning("No such level exists in list");
            return -1;
        }
        return m_DataStore.GetTimeForLevel(id);
    }

    /// <summary>
    /// Demo test methods
    /// </summary>
    public void PauseGame()
    {
        Time.timeScale = 0;
    }

    /// <summary>
    /// Demo test methods
    /// </summary>
    public void UnPauseGame()
    {
        Time.timeScale = 1;
    }

    /// <summary>
    /// Get the saved level the player is currently at
    /// </summary>
    /// <returns></returns>
    public int GetPlayerLevel()
    {
        return m_DataStore.GameData.currentLevel;
    }

    /// <summary>
    /// Gets the total xp the player has acquired
    /// Displayed on UI during gameplay
    /// </summary>
    /// <returns></returns>
    public int GetPlayerXP()
    {
        return m_DataStore.GameData.totalXp;
    }
                   
    /// <summary>
    /// Gets the total time player spent playing
    /// </summary>
    /// <returns></returns>
    public float GetTime()
    {
        return m_DataStore.GameData.totalTime;
    }

    /// <summary>
    /// Get total number of enemies killed
    /// </summary>
    /// <returns></returns>
    public int GetKills()
    {
        return m_DataStore.GameData.enemiesKilled;
    }

    /// <summary>
    /// Gets damage done for display
    /// </summary>
    /// <returns></returns>
    public float GetDamageDone()
    {
        return m_DataStore.GameData.damageDone;
    }

    /// <summary>
    /// Gets damage taken for display
    /// </summary>
    /// <returns></returns>
    public float GetDamageTaken()
    {
        return m_DataStore.GameData.damageTaken;
    }

    /// <summary>
    /// Gets stones collected for display
    /// </summary>
    /// <returns></returns>
    public int GetStonesCollected()
    {
        return m_DataStore.GameData.eyrieStones;
    }

    /// <summary>
    /// Get player hits for display
    /// </summary>
    /// <returns></returns>
    public float GetHits()
    {
        return m_DataStore.GameData.totalHits;
    }

    /// <summary>
    /// Get player score for display
    /// </summary>
    /// <returns></returns>
    public int GetScore()
    {
        return m_DataStore.GameData.totalScore;
    }

    /// <summary>
    /// Get player experience for display
    /// </summary>
    /// <returns></returns>
    public float GetExperienceNormalized()
    {
        return (float)m_DataStore.GameData.totalXp / m_DataStore.GameData.xpToNextLevel;
    }

    /// <summary>
    /// Method used for purchasing a weapon
    /// </summary>
    /// <param name="id">id for the weapon to purchase</param>
    /// <param name="weaponList">list of weapons to purchase from</param>
    public void PurchaseWeapon(string id, LootItem weapon, ItemList weaponList)
    {
        if (!weaponList.ContainsKey(id))
        {
            return;
        }
        m_DataStore.PurchaseWeapon(id, weapon);
        SaveData();
    }

    /// <summary>
    /// Method used for upgrading selected weapon
    /// </summary>
    /// <param name="id">weapon id</param>
    /// <param name="weapon">weapon object to upgrade</param>
    /// <param name="weaponList">list from which to upgrade</param>
    public void UpgradeWeapon(string id, LootItem weapon, ItemList weaponList)
    {
        if (!weaponList.ContainsKey(id))
        {
            return;
        }
        m_DataStore.UpgradeWeapon(weapon);
        SaveData();
    }

    /// <summary>
    /// Determines whether specified weapon has been purchased
    /// </summary>
    /// <param name="id">the weapon id</param>
    /// <param name="weaponList">list of weapons to check from</param>
    /// <returns></returns>
    public bool IsWeaponPurchased(string id, ItemList weaponList)
    {
        if (!weaponList.ContainsKey(id))
        {
            return false;
        }
        return m_DataStore.IsWeaponPurchased(id);
    }

    /// <summary>
    /// Purchases a booster item, and adds it to inventory
    /// </summary>
    /// <param name="id"></param>
    /// <param name="item"></param>
    /// <param name="list"></param>
    public void PurchaseItem(string id, LootItem item, ItemList list)
    {
        if (!list.ContainsKey(id))
        {
            return;
        }
        m_DataStore.PurchaseItem(id, item);
        SaveData();
    }

    /// <summary>
    /// Remove item from the list of purchased items
    /// </summary>
    /// <param name="id"></param>
    public void RemoveItemFromList(string id)
    {
        m_DataStore.RemoveItem(id);
        SaveData();
    }

    /// <summary>
    /// Updates current gold in database
    /// </summary>
    /// <param name="amount"></param>
    public void UpdateGold(int amount)
    {
        m_DataStore.GameData.UpdateGold(amount);
        SaveData();
    }

    /// <summary>
    /// Used to update the database by the currency only 
    /// </summary>
    /// <param name="amount"></param>
    public void UpdateGems(int amount)
    {
        m_DataStore.GameData.UpdateGems(amount);
        SaveData();
    }

    /// <summary>
    /// Update the current amount of gold in the persistent data store
    /// </summary>
    private void OnGoldChanged()
    {
        Gold.currentAmount = m_DataStore.GameData.gold;
    }

    /// <summary>
    /// Update the current amount of gems in the persistent data store
    /// </summary>
    private void OnGemsChanged()
    {
        Gems.currentAmount = m_DataStore.GameData.gems;
    }

    /// <summary>
    /// Set remove ads to true and persist that condition
    /// </summary>
    public void RemoveAds()
    {
        m_DataStore.removeAds = true;
        SaveData();
    }

    /// <summary>
    /// Returns whether ads are removed or not
    /// </summary>
    /// <returns></returns>
    public bool AdsRemoved()
    {
        return m_DataStore.removeAds;
    }

    /// <summary>
    /// mimicks subtraction of currency
    /// Test if currency data is saved correctly
    /// </summary>
    public void DepleteCurrency()
    {
        Gold.ChangeCurrency(-Random.Range(10, 50));
        Gems.ChangeCurrency(-Random.Range(5, 25));
    }

    /// <summary>
    /// mimicks addition of currency
    /// Test if currency data us saved correctly
    /// </summary>
    public void AddCurrency()
    {
        Gold.ChangeCurrency(Random.Range(10, 50));
        Gems.ChangeCurrency(Random.Range(5, 25));
    }

    /// <summary>
    /// Opens an instagram account
    /// </summary>
    public void OpenInstagramUrl()
    {
        Application.OpenURL(InstagramUrl);
    }

    /// <summary>
    /// Opens a twitter account
    /// </summary>
    public void OpenTwitterUrl()
    {
        Application.OpenURL(TwitterUrl);
    }

    /// <summary>
    /// Opens the how to play video
    /// </summary>
    public void OpeYoutubeUrl()
    {
        Application.OpenURL(YoutubeUrl);
    }

}
