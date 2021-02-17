using UnityEngine;
using UnityEngine.UI;


public class ItemToolTip : MonoBehaviour
{
    /// <summary>
    /// The selected item
    /// </summary>
    public Inventory.InventoryEntry SelectedItem { get; set; }

    /// <summary>
    /// Image icon of selected item
    /// </summary>
    public Image ItemImage;

    /// <summary>
    /// Name of selected item
    /// </summary>
    public Text ItemName;

    /// <summary>
    /// Item Description 
    /// </summary>
    public Text ItemDescription;

    /// <summary>
    /// Tells whether selected weapon is equipped or not
    /// </summary>
    public Text WeaponEquipedText;

    /// <summary>
    /// Uses the selected item when clicked
    /// </summary>
    public Button UseItemButton;

    /// <summary>
    /// Button Label
    /// </summary>
    public Text ButtonText;

    public InventoryUI Owner { get; set; }

    public void UseItem()
    {
        Owner.Character.Inventory.UseItem(SelectedItem);
        Owner.ObjectClicked(SelectedItem);
    }
}
