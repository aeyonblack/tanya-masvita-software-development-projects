using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

/// <summary> 
/// The data for each character in the game including the player and enemies.
/// Most systems connected to the player like the Inventory will be initialized here.
/// </summary>
public class CharacterData : MonoBehaviour
{
    #region Fields

    /// <summary>
    /// Name of this character
    /// </summary>
    public string CharacterName;

    /// <summary>
    /// The Inventory object
    /// </summary>
    public Inventory Inventory = new Inventory();

    /// <summary>
    /// TODO : Move this to UI Manager
    /// All UI will be handled by the UIManager
    /// </summary>
    public InventoryUI InventoryWindow;

    /// <summary>
    /// TODO : Move this to UI Manager
    /// </summary>
    public HotbarUI HotbarWindow;

    /// <summary>
    /// Stores character experience data
    /// </summary>
    public CharacterExperience Experience = new CharacterExperience();

    /// <summary>
    /// Starting weapons
    /// </summary>
    [HideInInspector] public WeaponConfiguration[] StartingWeapons = new WeaponConfiguration[3];

    /// <summary>
    /// Current equiped weapon
    /// </summary>
    public Weapon CurrentWeapon { get; private set; }

    /// <summary>
    /// keeps track of whether player is healing or not
    /// </summary>
    private bool healing;

    /// <summary>
    /// true when the player uses armor
    /// </summary>
    private bool boostingArmor;

    /// <summary>
    /// Health ref
    /// </summary>
    private float healthRef;

    /// <summary>
    /// Reference to the player's health
    /// </summary>
    private float armorRef;

    [Header("UI")]
    public Slider HealthSlider;
    public Slider ArmorSlider;
    public Color Flash = new Color(1f, 0f, 0f, 0.1f);
    public float FlashSpeed;
    public Image DamageImage;
    public Slider EncumbranceSlider;

    public AudioClip EquipClip;

    /// <summary>
    /// Plays given clips at random
    /// </summary>
    public RandomPlayer FootstepPlayer;

    /// <summary>
    /// Scriptable object holding player stats
    /// </summary>
    public CharacterStats Stats;

    /// <summary>
    /// Invoked when player dies
    /// Fires kill events
    /// </summary>
    public event Action Dead;

    /// <summary>
    /// Played when player levels up
    /// </summary>
    public AudioClip LevelUpSound;

    /// <summary>
    /// Better method of doing this exists
    /// </summary>
    private bool hit = false;

    #endregion

    #region Initializations

    /// <summary>
    /// Initializes the character data.
    /// Initializes the inventory and equips all starting weapons.
    /// The first weapon in the list is the one which will be equiped
    /// when the game loads
    /// </summary>
    public void Initialize()
    {
        Stats.currentHealth = 100;
        Stats.alive = true;

        Controller.instance.WeaponPosition.gameObject.SetActive(true);

        if (GameManager.instance == null)
        {
            return;
        }

        for (int i = 0; i < GameManager.instance.StartingWeapons.Length; i++)
        {
            StartingWeapons[i] = GameManager.instance.StartingWeapons[i];
        }

        LoadPlayerStats();
        Inventory.Initialize(this);
        Experience.Initialize(this);

        // This needs to be done in some Manager script
        InventoryWindow.Init();
        
        for (int i = 0; i < StartingWeapons.Length; ++i)
        {
            if (StartingWeapons[i])
            {
                Equip(StartingWeapons[i]);
                break;
            }
        }

        for (int i = 0; i < StartingWeapons.Length; ++i)
        {
            if (StartingWeapons[i] != null)
            {
                Inventory.AddItem(StartingWeapons[i]);
            }
        }

  

        // Add all purchased items to inventory for use
        foreach (ShopSaveData data in GameManager.instance.PurchasedItems)
        {
            LootItem item = data.item;
            Inventory.AddItem(item);
        }

        InventoryWindow.Load(this);
        HotbarWindow.Init(this);
        HotbarWindow.Load();

        ArmorSlider.maxValue = Stats.maxResistance;

        HealthSlider.value = Stats.currentHealth;
        ArmorSlider.value = Stats.currentResistance;
        healthRef = Stats.currentHealth;
        armorRef = Stats.currentResistance;

        UpdateEncumbranceUI();
    }

    #endregion

    /// <summary>
    /// Open and close Inventory
    /// TODO : Move this to UI Manager
    /// </summary>
    public void ToggleInventory()
    {
        if (InventoryWindow.gameObject.activeSelf)
        {
            InventoryWindow.gameObject.SetActive(false);
        }
        else
        {
            InventoryWindow.gameObject.SetActive(true);
        }
    }

    /// <summary>
    /// Change slider value
    /// </summary>
    public void UpdateEncumbranceUI()
    {
        float totalEncumbrance = Stats.currentEncumbrance + Stats.currentWeaponEncumbrance;
        float currentFreeSpace = Inventory.FreeSpace + Inventory.WeaponSpace;
        EncumbranceSlider.value = ((totalEncumbrance - currentFreeSpace) / totalEncumbrance) * 100;
    }

    /// <summary>
    /// Get saved data
    /// Used to keep track of the player level and for display in UI
    /// </summary>
    public void LoadPlayerStats()
    {
        Stats.currentLevel = GameManager.instance.GetPlayerLevel();
        Stats.experiencePoints = GameManager.instance.GetPlayerXP();
        Stats.currentHealth = Stats.maxHealth;
        Stats.currentEncumbrance = Stats.maxEncumbrance;
    }

    #region Update

    private void Update()
    {

        if (healing)
        {
            if (healthRef < Stats.currentHealth)
            {
                healthRef += 1;
                HealthSlider.value = healthRef;
            }
            else
            {
                healing = false;
            }
        }

        if (boostingArmor)
        {
            if (armorRef < Stats.currentResistance)
            {
                armorRef += 1;
                ArmorSlider.value = armorRef;
            }
            else
            {
                boostingArmor = false;
            }
        }

        if (hit)
        {
            DamageImage.color = Flash;
        }
        else
        {
            DamageImage.color = Color.Lerp(DamageImage.color, Color.clear, FlashSpeed * Time.deltaTime);
        }
        // Player can only be hit once at some instant
        hit = false;
    }

    #endregion

    #region Equiping and Unequiping Weapons

    /// <summary>
    /// Equip new weapon
    /// </summary>
    public bool EquipWeapon(WeaponConfiguration weapon)
    {
        if (UnequipWeapon())
        {
            Equip(weapon);
            return true;
        }
        return false; 
    }

    /// <summary>
    /// Equip weapon method used to pickup starting weapon
    /// and inside an equip method for picking up new weapons during gameplay
    /// </summary>
    /// <param name="weapon">Weapon to equip</param>
    private void Equip(WeaponConfiguration weapon)
    {
        if (weapon == null)
        {
            return;
        }

        if (CurrentWeapon == null)
        {
            var obj = Instantiate(weapon.weaponPrefab.gameObject, Controller.instance.WeaponPosition, false);
            
            obj.name = weapon.weaponPrefab.name;
            CurrentWeapon = obj.GetComponent<Weapon>();
            CurrentWeapon.PickedUp(this);
            CurrentWeapon.Selected();
            Helpers.RecursiveLayerChange(obj.transform, LayerMask.NameToLayer("Weapon"));
        }
        else
        {
            if (CurrentWeapon.weaponConfiguration == weapon)
            {
                // TODO : Fill the clip of this weapon
            }
            else
            {
                Instantiate(CurrentWeapon, Controller.instance.WeaponPosition, false);
                CurrentWeapon.Selected();
            }
        }
        Controller.instance.AudioSource.PlayOneShot(EquipClip);
    }
      
    /// <summary>
    /// Add item to inventory and destroy it from the scene
    /// </summary>
    public bool UnequipWeapon()
    {
        if (CurrentWeapon != null)
        {
            CurrentWeapon.PutAway();
            //Inventory.AddItem(CurrentWeapon.weaponConfiguration);
            Destroy(CurrentWeapon.gameObject);
            CurrentWeapon = null;
            Debug.Log("[CharacterData] Weapon Unequiped");
        }
        return true;
    }

    #endregion

    #region Ammo Loading

    /// <summary>
    /// Load ammo to weapon
    /// </summary>
    /// <param name="ammo">ammo to be loaded</param>
    public void LoadAmmo(Ammo ammo)
    {
        //Debug.Log("Updating ammo amount by " + ammo.AmountToAdd);
        CurrentWeapon.weaponConfiguration.Ammo.UpdateAmount(ammo.AmountToAdd, Stats);
        CurrentWeapon.Reload();
    }

    #endregion

    #region Attacks and Damage

    /// <summary>
    /// Add or subtract the player's health
    /// </summary>
    /// <param name="health"></param>
    public void ModifyHealth(float health)
    {
        Stats.ModifyHealth(health);
        healing = true;
    }

    /// <summary>
    /// Add or subtract the player's resistance to damage
    /// </summary>
    /// <param name="amount"></param>
    public void ModifyResistance(float amount)
    {
        Stats.ModifyResistance(amount);
        boostingArmor = true;
    }

    /// <summary>
    /// The player is hit, take damage
    /// </summary>
    /// <param name="attack"></param>
    public void TakeDamage(Attack attack)
    {
        hit = true;
        Stats.Damage(attack, HealthSlider, ArmorSlider);
        LevelManager.instance.LevelStats.AddDamageTaken(attack.Damage);

        if (!healing)
        {
            // Only set health ref if player is not currently healing
            healthRef = Stats.currentHealth;
        }

        if (!boostingArmor)
        {
            // Only set armorRef if player is not currently boosting
            armorRef = Stats.currentResistance;
        }


    }

    /// <summary>
    /// Kill the player
    /// </summary>
    public void Death()
    {
        Controller.instance.WeaponPosition.gameObject.SetActive(false);
        Stats.Death();
    }

    #endregion

    #region Footsteps

    /// <summary>
    /// Play footsteps when player is walking
    /// </summary>
    public void PlayFootsteps()
    {
        FootstepPlayer.PlayRandom();
    }

    #endregion

}
