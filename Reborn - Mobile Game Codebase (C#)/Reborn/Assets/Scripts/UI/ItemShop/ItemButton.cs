using UnityEngine;
using UnityEngine.UI;

/// <summary>
/// The button for purchasing an item
/// </summary>
[RequireComponent(typeof(Button))]
public class ItemButton : MonoBehaviour
{
    /// <summary>
    /// Background image for this item 
    /// </summary>
    public Image BackgroundImage;

    /// <summary>
    /// Image for the current item
    /// </summary>
    public Image ItemImage;

    /// <summary>
    /// Image for the currency used to purchase this item
    /// </summary>
    public Image CurrencyImage;

    /// <summary>
    /// Name of this item
    /// </summary>
    public Text ItemName;

    /// <summary>
    /// Cost of this item
    /// </summary>
    public Text ItemCost;

    /// <summary>
    /// Item Preview window
    /// </summary>
    public PreviewWindow PreviewWindow;

    /// <summary>
    /// The data concerning the item this button represents
    /// </summary>
    protected LootItem m_Item;

    /// <summary>
    /// List of shop items
    /// </summary>
    protected ItemList m_List;

    private Animator m_CamAnim;

    private Transform m_ParentUI;

    /// <summary>
    /// Invoked when the button is clicked
    /// </summary>
    public void ButtonClicked()
    {
        OpenPreviewWindow();
    }

    /// <summary>
    /// Assign data from item to button
    /// </summary>
    /// <param name="item"></param>
    public void Initialize(LootItem item, ItemList list, Animator animator, Transform parentUI)
    {
        if (ItemName == null || ItemCost == null)
        {
            return;
        }

        m_List = list;
        m_Item = item;

        ItemName.text = item.itemName;
        ItemCost.text = item.itemCost.ToString();
        ItemImage.sprite = item.itemShopPoster != null ? item.itemShopPoster : item.itemSprite;
        CurrencyImage.sprite = item.currencySprite;

        byte r = (byte)Random.Range(0, 255);
        byte g = (byte)Random.Range(0, 255);
        byte b = (byte)Random.Range(0, 255);

        BackgroundImage.color = new Color32(r, g, b, 150);

        m_CamAnim = animator;
        m_ParentUI = parentUI;
    }

    /// <summary>
    /// Opens the window to preview this item
    /// </summary>
    protected void OpenPreviewWindow()
    {
        if (PreviewWindow != null)
        {
            m_ParentUI.gameObject.SetActive(false);
            Instantiate(PreviewWindow);
            PreviewWindow.instance.previewItem = m_Item;
            PreviewWindow.instance.ItemList = m_List;
            PreviewWindow.instance.animator = m_CamAnim;
            PreviewWindow.instance.menuUI = m_ParentUI;

            if (m_CamAnim != null)
            {
                m_CamAnim.SetTrigger("Show");
            }
        }
    }

}
