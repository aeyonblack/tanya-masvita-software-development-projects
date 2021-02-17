using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

/// <summary>
/// Manages the interface for selecting starting weapons
/// </summary>
public class StartingWeaponScreen : MainMenuPage
{
    /// <summary>
    /// Shows a list of owned pistols to select from
    /// </summary>
    public Button SelectPistolButton;

    /// <summary>
    /// Shows a suitable list of weapons to select from
    /// </summary>
    public Button SelectWeaponOneButton;

    /// <summary>
    /// Shows a suitable list of weapons to select from
    /// </summary>
    public Button SelectWeaponTwoButton;

    public Image SlotOneImage;

    public Image SlotTwoImage;

    public Image SlotThreeImage;

    /// <summary>
    /// Name of selected pistol
    /// </summary>
    public Text PistolName;

    /// <summary>
    /// Name of selected weapon in slot 2
    /// </summary>
    public Text WeaponOneName;

    /// <summary>
    /// Name of selected weapon in slot 3
    /// </summary>
    public Text WeaponTwoName;

    /// <summary>
    /// Weapon button
    /// </summary>
    public SelectWeaponButton SelectionButtonPrefab;

    public LayoutGroup Layout;

    public Transform RightBuffer;

    public Button BackButton;

    public ItemList WeaponList;

    private List<WeaponShopSaveData> m_PurchasedWeaponList;

    protected List<Button> m_Buttons;

    private static int target;

    protected virtual void Start()
    {
        m_PurchasedWeaponList = GameManager.instance.PurchasedWeapons;
        ShowPistols();
    }

    private void ClearLayout()
    {
        for (int i = 0; i < Layout.transform.childCount; i++)
        {
            Destroy(Layout.transform.GetChild(i).gameObject);
        }
    }

    public void ShowPistols()
    {
        if (GameManager.instance == null)
        {
            return;
        }

        if (SelectionButtonPrefab == null || Layout == null || m_PurchasedWeaponList == null)
        {
            return;
        }

        ClearLayout();
        m_Buttons = new List<Button>();

        int count = WeaponList.Count;

        for (int i = 0; i < m_PurchasedWeaponList.Count; i++)
        {
            for (int j = 0; j < count; j++)
            {
                if (m_PurchasedWeaponList[i].id == WeaponList[j].id)
                {
                    WeaponConfiguration weapon = WeaponList[j].Item as WeaponConfiguration;
                    if (weapon != null && (weapon.Ammo.Type == Ammo.AmmoType.PISTOL))
                    {
                        SelectWeaponButton button = CreatePistolButton(weapon);
                        button.transform.SetParent(Layout.transform);
                        button.transform.localScale = Vector3.one;
                        m_Buttons.Add(button.GetComponent<Button>());
                    }
                }
            }
        }

        if (m_Buttons.Count == 0)
        {
            return;
        }

        if (RightBuffer != null)
        {
            RightBuffer.SetAsLastSibling();
        }

        for (int k = 1; k < m_Buttons.Count - 1; k++)
        {
            Button button = m_Buttons[k];
            SetupNavigation(button, m_Buttons[k - 1], m_Buttons[k + 1]);
        }

        //SetupNavigation(m_Buttons[0], BackButton, m_Buttons[1]);
        //SetupNavigation(m_Buttons[m_Buttons.Count - 1], m_Buttons[m_Buttons.Count - 2], null);

    }

    public void ShowOtherWeaponsOne()
    {
        if (GameManager.instance == null)
        {
            return;
        }

        if (SelectionButtonPrefab == null || Layout == null || m_PurchasedWeaponList == null)
        {
            return;
        }

        ClearLayout();
        m_Buttons = new List<Button>();

        int count = WeaponList.Count;

        for (int i = 0; i < m_PurchasedWeaponList.Count; i++)
        {
            for (int j = 0; j < count; j++)
            {
                if (m_PurchasedWeaponList[i].id == WeaponList[j].id)
                {
                    WeaponConfiguration weapon = WeaponList[j].Item as WeaponConfiguration;
                    if (weapon != null && (weapon.Ammo.Type == Ammo.AmmoType.RIFLE))
                    {
                        SelectWeaponButton button = CreateWeaponButtonOne(weapon);
                        button.transform.SetParent(Layout.transform);
                        button.transform.localScale = Vector3.one;
                        if (!m_Buttons.Contains(button.GetComponent<Button>()))
                        {
                            m_Buttons.Add(button.GetComponent<Button>());
                        }
                    }
                }
            }
        }

        if (m_Buttons.Count == 0)
        {
            return;
        }

        if (RightBuffer != null)
        {
            RightBuffer.SetAsLastSibling();
        }

        for (int k = 1; k < m_Buttons.Count - 1; k++)
        {
            Button button = m_Buttons[k];
            SetupNavigation(button, m_Buttons[k - 1], m_Buttons[k + 1]);
        }

        //SetupNavigation(m_Buttons[0], BackButton, m_Buttons[1]);
        //SetupNavigation(m_Buttons[m_Buttons.Count - 1], m_Buttons[m_Buttons.Count - 2], null);
    }

    public void ShowOtherWeaponsTwo()
    {
        if (GameManager.instance == null)
        {
            return;
        }

        if (SelectionButtonPrefab == null || Layout == null || m_PurchasedWeaponList == null)
        {
            return;
        }

        ClearLayout();
        m_Buttons = new List<Button>();

        int count = WeaponList.Count;

        for (int i = 0; i < m_PurchasedWeaponList.Count; i++)
        {
            for (int j = 0; j < count; j++)
            {
                if (m_PurchasedWeaponList[i].id == WeaponList[j].id)
                {
                    WeaponConfiguration weapon = WeaponList[j].Item as WeaponConfiguration;
                    if (weapon != null && (weapon.Ammo.Type != Ammo.AmmoType.PISTOL && 
                        weapon.Ammo.Type != Ammo.AmmoType.RIFLE))
                    {
                        SelectWeaponButton button = CreateWeaponButtonTwo(weapon);
                        button.transform.SetParent(Layout.transform);
                        button.transform.localScale = Vector3.one;
                        if (!m_Buttons.Contains(button.GetComponent<Button>()))
                        {
                            m_Buttons.Add(button.GetComponent<Button>());
                        }
                    }
                }
            }
        }

        if (m_Buttons.Count == 0)
        {
            return;
        }

        if (RightBuffer != null)
        {
            RightBuffer.SetAsLastSibling();
        }

        for (int k = 1; k < m_Buttons.Count - 1; k++)
        {
            Button button = m_Buttons[k];
            SetupNavigation(button, m_Buttons[k - 1], m_Buttons[k + 1]);
        }

        //SetupNavigation(m_Buttons[0], BackButton, m_Buttons[1]);
        //SetupNavigation(m_Buttons[m_Buttons.Count - 1], m_Buttons[m_Buttons.Count - 2], null);
    }

    public SelectWeaponButton CreatePistolButton(LootItem weapon, int targetSlot = 0)
    {
        SelectWeaponButton button = Instantiate(SelectionButtonPrefab);
        button.Initialize(weapon, SlotOneImage, SlotTwoImage, SlotThreeImage, targetSlot);
        button.SetupWeaponNames(PistolName, WeaponOneName, WeaponTwoName);
        return button;
    }

    public SelectWeaponButton CreateWeaponButtonOne(LootItem weapon, int targetSlot = 1)
    {
        SelectWeaponButton button = Instantiate(SelectionButtonPrefab);
        button.Initialize(weapon, SlotOneImage, SlotTwoImage, SlotThreeImage, targetSlot);
        button.SetupWeaponNames(PistolName, WeaponOneName, WeaponTwoName);
        return button;
    }

    public SelectWeaponButton CreateWeaponButtonTwo(LootItem weapon, int targetSlot = 2)
    {
        SelectWeaponButton button = Instantiate(SelectionButtonPrefab);
        button.Initialize(weapon, SlotOneImage, SlotTwoImage, SlotThreeImage, targetSlot);
        button.SetupWeaponNames(PistolName, WeaponOneName, WeaponTwoName);
        return button;
    }

    public override void Show()
    {
        base.Show();
    }

    public override void Hide()
    {
        base.Hide();
    }

    /// <summary>
    /// Sets up navigation for a selectable
    /// </summary>
    /// <param name="selectable"></param>
    /// <param name="selectableLeft"></param>
    /// <param name="selectableRight"></param>
    private void SetupNavigation(Selectable selectable, Selectable left, Selectable right)
    {
        Navigation navigation = selectable.navigation;
        navigation.selectOnLeft = left;
        navigation.selectOnRight = right;
        selectable.navigation = navigation;
    }
}
