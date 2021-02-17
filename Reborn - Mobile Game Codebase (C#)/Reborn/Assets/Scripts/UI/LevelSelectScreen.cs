using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

/// <summary>
/// A manager for the level select interface
/// </summary>
public class LevelSelectScreen : MainMenuPage
{
    /// <summary>
    /// Represents the level select buttons
    /// </summary>
    public LevelSelectButton SelectionPrefab;

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
    /// Group of main menu buttons
    /// </summary>
    public Transform MenuButtons;

    /// <summary>
    /// Plays camera animations
    /// </summary>
    public Animator Animator;

    /// <summary>
    /// The reference to the list of levels to display
    /// </summary>
    protected LevelList m_LevelList;

    /// <summary>
    /// List of level buttons
    /// </summary>
    protected List<Button> m_Buttons = new List<Button>();


    /// <summary>
    /// Instantiate the buttons 
    /// </summary>
    protected virtual void Start()
    {
        if (GameManager.instance == null)
        {
            return;
        }

        m_LevelList = GameManager.instance.LevelList;

        if (LayoutGroup == null || SelectionPrefab == null || m_LevelList == null)
        {
            return;
        }

        int amount = m_LevelList.Count;

        for (int i = 0; i < amount; i++)
        {
            LevelSelectButton button = CreateButton(m_LevelList[i]);
            button.transform.SetParent(LayoutGroup.transform);
            button.transform.localScale = Vector3.one;
            m_Buttons.Add(button.GetComponent<Button>());
        }

        if (RightBuffer != null)
        {
            RightBuffer.SetAsLastSibling();
        }

        for (int k = 1; k < m_Buttons.Count-1; k++)
        {
            Button button = m_Buttons[k];
            SetupNavigation(button, m_Buttons[k - 1], m_Buttons[k + 1]);
        }

        SetupNavigation(m_Buttons[0], BackButton, m_Buttons[1]);
        SetupNavigation(m_Buttons[m_Buttons.Count - 1], m_Buttons[m_Buttons.Count - 2], null);
        // Scrolling to do
    }

    /// <summary>
    /// Create and instantiate a level select button base on item
    /// </summary>
    /// <param name="item">The level data</param>
    /// <returns>The created button</returns>
    protected LevelSelectButton CreateButton(LevelItem item)
    {
        LevelSelectButton button = Instantiate(SelectionPrefab);
        button.Initialize(item);
        return button;
    }

    /// <summary>
    /// Play camera animations
    /// </summary>
    public override void Show()
    {
        base.Show();

        if (Animator != null)
        {
            Animator.SetTrigger("Show");
        }

        MenuButtons.gameObject.SetActive(false);
    }

    /// <summary>
    /// Return camera to normal position
    /// </summary>
    public override void Hide()
    {
        base.Hide();

        if (Animator != null)
        {
            Animator.SetTrigger("Hide");
        }

        MenuButtons.gameObject.SetActive(true);
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
