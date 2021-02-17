using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class BaseItemScreen : MainMenuPage
{
    /// <summary>
    /// Parent of this screen
    /// </summary>
    public Transform Parent;

    /// <summary>
    /// Represents the item buttons
    /// </summary>
    public ItemButton ItemButtonPrefab;

    /// <summary>
    /// Layout to instantiate the level buttons in
    /// </summary>
    public LayoutGroup LayoutGroup;

    /// <summary>
    /// A buffer for the level select buttons
    /// </summary>
    public Transform RightBuffer;

    /// <summary>
    /// Loads previous menu page when clicked
    /// </summary>
    public Button BackButton;

    /// <summary>
    /// A list of all items
    /// </summary>
    public ItemList ItemList;

    /// <summary>
    /// Used to play camera animations
    /// </summary>
    public Animator CameraAnimator;

    /// <summary>
    /// List of item Buttons
    /// </summary>
    protected List<Button> m_Buttons = new List<Button>();

    protected virtual void Start()
    {
        if (LayoutGroup == null || ItemButtonPrefab == null || ItemList == null)
        {
            return;
        }

        int count = ItemList.Count;

        for (int i = 0; i < count; i++)
        {
            ItemButton button = CreateButton(ItemList[i].Item);
            button.transform.SetParent(LayoutGroup.transform);
            button.transform.localScale = Vector3.one;
            m_Buttons.Add(button.GetComponent<Button>());
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
        SetupNavigation(m_Buttons[0], BackButton, m_Buttons[1]);
        SetupNavigation(m_Buttons[m_Buttons.Count - 1], m_Buttons[m_Buttons.Count - 2], null);
    }

    protected ItemButton CreateButton(LootItem item)
    {
        ItemButton button = Instantiate(ItemButtonPrefab);
        button.Initialize(item, ItemList, CameraAnimator, Parent);
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
