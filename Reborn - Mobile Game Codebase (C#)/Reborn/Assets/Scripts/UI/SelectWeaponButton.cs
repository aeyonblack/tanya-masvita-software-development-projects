using UnityEngine;
using UnityEngine.UI;

/// <summary>
/// The button for selecting a weapon
/// </summary>
public class SelectWeaponButton : MonoBehaviour
{
    /// <summary>
    /// Image for the this weapon
    /// </summary>
    public Image WeaponImage;

    /// <summary>
    /// Reference to the name of the pistol in the first slot
    /// </summary>
    private Text m_PistolName;

    /// <summary>
    /// Reference to the name of the weapon in the second slot
    /// </summary>
    private Text m_WeaponOneName;

    /// <summary>
    /// Reference to the name of the weapon in the third slot
    /// </summary>
    private Text m_WeaponTwoName;

    /// <summary>
    /// Data concerning the weapon this button displays
    /// </summary>
    private LootItem m_Weapon;

    /// <summary>
    /// Image of the weapon to be placed in slot 1 
    /// </summary>
    private Image m_SlotOneImage;

    /// <summary>
    /// Image of the weapon to be placed in slot 2
    /// </summary>
    private Image m_SlotTwoImage;

    /// <summary>
    /// Image of the weapon to be placed in slot 3
    /// </summary>
    private Image m_SlotThreeImage;

    /// <summary>
    /// The slot that a selected weapon should be placed in 
    /// </summary>
    private int m_TargetSlot;

    /// <summary>
    /// Assign data to button
    /// </summary>
    /// <param name="weapon"></param>
    public void Initialize(LootItem weapon, Image slotOne, Image slotTwo, Image slotThree, int target)
    {
        m_Weapon = weapon;
        WeaponImage.sprite = weapon.itemSprite;

        m_SlotOneImage = slotOne;
        m_SlotTwoImage = slotTwo;
        m_SlotThreeImage = slotThree;

        m_TargetSlot = target;
    }

    public void SetupWeaponNames(Text pistolName, Text weaponOneName, Text weaponTwoName)
    {
        m_PistolName = pistolName;
        m_WeaponOneName = weaponOneName;
        m_WeaponTwoName = weaponTwoName;
    }

    /// <summary>
    /// Invoked when button is clicked
    /// </summary>
    public void ButtonClicked()
    {
        switch (m_TargetSlot)
        {
            case 0:
                SelectPistol();
                break;
            case 1:
                SelectWeaponOne();
                break;
            case 2:
                SelectWeaponTwo();
                break;
        }
    }

    /// <summary>
    /// Update the UI related to this button when button is clicked
    /// Let weapon in slot 1 = pistol
    /// </summary>
    public void SelectPistol()
    {
        m_SlotOneImage.gameObject.SetActive(true);
        m_SlotOneImage.sprite = m_Weapon.itemSprite;
        m_PistolName.text = m_Weapon.itemName;
        GameManager.instance.StartingWeapons[0] = m_Weapon as WeaponConfiguration;
        Debug.Log("Slot 1 = " + GameManager.instance.StartingWeapons[0].itemName);
    }

    /// <summary>
    /// Set the weapon in slot 2 to the current weapon
    /// </summary>
    public void SelectWeaponOne()
    {
        m_SlotTwoImage.gameObject.SetActive(true);
        m_SlotTwoImage.sprite = m_Weapon.itemSprite;
        m_WeaponOneName.text = m_Weapon.itemName;
        GameManager.instance.StartingWeapons[1] = m_Weapon as WeaponConfiguration;
    }

    /// <summary>
    /// Set the weapon in slot 3 to the current weapon
    /// </summary>
    public void SelectWeaponTwo()
    {
        m_SlotThreeImage.gameObject.SetActive(true);
        m_SlotThreeImage.sprite = m_Weapon.itemSprite;
        m_WeaponTwoName.text = m_Weapon.itemName;
        GameManager.instance.StartingWeapons[2] = m_Weapon as WeaponConfiguration;
    }
}
