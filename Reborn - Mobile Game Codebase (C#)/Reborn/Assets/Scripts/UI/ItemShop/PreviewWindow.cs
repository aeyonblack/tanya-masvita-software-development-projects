using UnityEngine;
using UnityEngine.UI;

/// <summary>
/// Displays a very simple preview interface before purchase
/// </summary>
public class PreviewWindow : Singleton<PreviewWindow>
{
    /// <summary>
    /// List of shop items
    /// </summary>
    [HideInInspector] public ItemList ItemList;

    /// <summary>
    /// Item to preview
    /// </summary>
    [HideInInspector] public LootItem previewItem;

    /// <summary>
    /// Used to play camera animations
    /// </summary>
    [HideInInspector] public Animator animator;

    /// <summary>
    /// Reference to the menu user interface
    /// </summary>
    [HideInInspector] public Transform menuUI;

    /// <summary>
    /// Image for the selected item
    /// </summary>
    public Image ItemImage;

    /// <summary>
    /// Image of the currency used for this item
    /// </summary>
    public Image CurrencyImage;

    /// <summary>
    /// Name of the selected item
    /// </summary>
    public Text ItemName;

    /// <summary>
    /// Description of the selected item
    /// </summary>
    public Text Description;

    /// <summary>
    /// Cost of the selected item
    /// </summary>
    public Text Cost;

    /// <summary>
    /// Required level for this item
    /// </summary>
    public Text Level;

    /// <summary>
    /// Purchases item when clicked
    /// </summary>
    public Button PurchaseButton;

    /// <summary>
    /// Upgrades weapon when clicked
    /// </summary>
    public Button UpgradeButton;

    /// <summary>
    /// Displayed when preview item is locked
    /// </summary>
    public Image ItemLockedImage;

    /// <summary>
    /// Played when item is purchased successfully
    /// </summary>
    public AudioClip PurchasedItemSound;

    private AudioSource audioSource; 

    private WeaponConfiguration m_Weapon;

    private void Start()
    {
        if (GameManager.instance == null)
        {
            return;
        }

        audioSource = GetComponent<AudioSource>();

        ItemImage.sprite = previewItem.itemSprite;
        CurrencyImage.sprite = previewItem.currencySprite;
        ItemName.text = previewItem.itemName;
        Cost.text = previewItem.itemCost.ToString();
        Level.text = "Level " + previewItem.requiredLevel.ToString();

        if (previewItem.requiredLevel > GameManager.instance.GetPlayerLevel())
        {
            ItemLockedImage.gameObject.SetActive(true);
            PurchaseButton.gameObject.SetActive(false);
            UpgradeButton.gameObject.SetActive(false);
            Description.text = "DESCRIPTION NOT AVAILABLE AT THE MOMENT. REACH LEVEL " + previewItem.requiredLevel + " TO UNLOCK.";
        }
        else
        {
            if (previewItem.itemType == ItemType.Weapon)
            {
                WeaponConfiguration weapon = previewItem as WeaponConfiguration;
                if (GameManager.instance.IsWeaponPurchased(previewItem.id, ItemList))
                {
                    UpgradeButton.gameObject.SetActive(true);
                    PurchaseButton.gameObject.SetActive(false);
                    ItemLockedImage.gameObject.SetActive(false);

                    UpgradeButton.interactable = false;
                   /** if (weapon.levels.Length != 0 && !weapon.isAtMaxLevel)
                    {
                        UpgradeButton.interactable = GameManager.instance.Gold.CanAfford(weapon.levels[weapon.currentLevel].cost);
                    }
                    else
                    {
                        UpgradeButton.interactable = false;
                    } **/
                }
                else
                {
                    PurchaseButton.gameObject.SetActive(true);
                    ItemLockedImage.gameObject.SetActive(false);
                    UpgradeButton.gameObject.SetActive(false);
                    Debug.Log("Not purchased");

                    PurchaseButton.interactable = GameManager.instance.Gold.CanAfford((int)weapon.itemCost);
                }
            }
            else
            {
                PurchaseButton.gameObject.SetActive(true);
                UpgradeButton.gameObject.SetActive(false);
                Level.gameObject.SetActive(false);
                ItemLockedImage.gameObject.SetActive(false);

                if (previewItem.currency == CurrencyType.Gems)
                {
                    PurchaseButton.interactable = GameManager.instance.Gems.CanAfford((int)previewItem.itemCost);
                }
                else
                {
                    PurchaseButton.interactable = GameManager.instance.Gold.CanAfford((int)previewItem.itemCost);
                }
            }
            Description.text = previewItem.itemDescription;
        }

        // Subscribe to currency change event
        GameManager.instance.Gold.currencyChanged += OnGoldChanged;
        GameManager.instance.Gems.currencyChanged += OnGemsChanged;
    }

    /// <summary>
    /// Update the UI when the player purchases a weapon
    /// </summary>
    private void Update()
    {
        if (GameManager.instance.IsWeaponPurchased(previewItem.id, ItemList) && !UpgradeButton.gameObject.activeSelf)
        {
            WeaponConfiguration weapon = previewItem as WeaponConfiguration;

            UpgradeButton.gameObject.SetActive(true);
            PurchaseButton.gameObject.SetActive(false);
            ItemLockedImage.gameObject.SetActive(false);

            //UpgradeButton.interactable = GameManager.instance.Gold.CanAfford(weapon.levels[weapon.currentLevel].cost);
            UpgradeButton.interactable = false;
        }
    }

    /// <summary>
    /// Invoked when button is clicked
    /// Calls the preview item's purchase method to attempt a purchase
    /// </summary>
    public void BuyItem()
    {
        if (previewItem.TryPurchaseItem())
        {
            if (previewItem.itemType == ItemType.Weapon)
            {
                GameManager.instance.PurchaseWeapon(previewItem.id, previewItem, ItemList);
            }
            else
            {
                if (previewItem.itemType == ItemType.Gem)
                    return;
                if (previewItem.itemType == ItemType.Gold)
                    BuyGold(); 
                else
                    GameManager.instance.PurchaseItem(previewItem.id, previewItem, ItemList);
            }
            audioSource.PlayOneShot(PurchasedItemSound);
        }
        else
        {
            Debug.Log("Purchase failed");
        }
    }

    /// <summary>
    /// Actual method used to purchase gold
    /// </summary>
    private void BuyGold()
    {
        GoldItem gold = previewItem as GoldItem;
        GameManager.instance.Gold.AddCurrency(gold.amountGold);
    }

    /// <summary>
    /// Upgrades the current previewed item
    /// </summary>
    public void UpgradeItem()
    {
        if (previewItem.itemType == ItemType.Weapon)
        {
            if (previewItem.UpgradeItem())
            {
                GameManager.instance.UpgradeWeapon(previewItem.id, previewItem, ItemList);
            }
        }
    }

    /// <summary>
    /// Check if the player can afford an upgrade to the next level
    /// if the preview item is a weapon
    /// </summary>
    private void OnGoldChanged()
    {
        if (previewItem.itemType == ItemType.Weapon)
        {
            WeaponConfiguration weapon = previewItem as WeaponConfiguration;

            if (weapon.levels.Length == 0 || weapon.isAtMaxLevel)
            {
                return;
            }

            if (GameManager.instance.Gold.CanAfford(weapon.levels[weapon.currentLevel].cost))
            {
                UpgradeButton.interactable = false;
            }
            else
            {
                UpgradeButton.interactable = true;
            }
        }
        else
        {
            if(PurchaseButton.gameObject.activeSelf)
            {
                PurchaseButton.interactable = GameManager.instance.Gold.CanAfford((int)previewItem.itemCost);
            }
        }
    }

    /// <summary>
    /// Check if the player can afford to purchase the current selected item
    /// </summary>
    private void OnGemsChanged()
    {
        if (PurchaseButton.gameObject.activeSelf)
        {
            PurchaseButton.interactable = GameManager.instance.Gems.CanAfford((int)previewItem.itemCost);
        }
    }

    /// <summary>
    /// Close this window
    /// Unsubscribe to events
    /// </summary>
    public void CloseWindow()
    {
        if (gameObject != null)
        {
            GameManager.instance.Gold.currencyChanged -= OnGoldChanged;
            GameManager.instance.Gems.currencyChanged -= OnGemsChanged;
            animator.SetTrigger("Hide");
            if (!menuUI.gameObject.activeSelf)
                menuUI.gameObject.SetActive(true);
            Destroy(gameObject);
        }
    }
}
