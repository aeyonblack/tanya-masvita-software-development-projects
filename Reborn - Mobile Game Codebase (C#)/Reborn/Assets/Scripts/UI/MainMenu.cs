using System.Collections.Generic;
using UnityEngine;

/// <summary>
/// Abstract base class for a Main Menu
/// </summary>
public abstract class MainMenu : MonoBehaviour
{
    /// <summary>
    /// Currently open page
    /// </summary>
    protected IMainMenuPage m_CurrentPage;

    /// <summary>
    /// Used to keep track of pages
    /// </summary>
    protected Stack<IMainMenuPage> m_PageStack = new Stack<IMainMenuPage>();

    /// <summary>
    /// Change page
    /// </summary>
    /// <param name="page">Page to transition to</param>
    protected virtual void ChangePage(IMainMenuPage page)
    {
        DeactivateCurrentPage();
        ActivatePage(page);
    }

    /// <summary>
    /// Deactivate currently open page
    /// </summary>
    protected void DeactivateCurrentPage()
    {
        if (m_CurrentPage != null)
        {
            m_CurrentPage.Hide();
        }
    }

    /// <summary>
    /// Activate specified page
    /// </summary>
    /// <param name="page">page to activate</param>
    protected void ActivatePage(IMainMenuPage page)
    {
        m_CurrentPage = page;
        m_CurrentPage.Show();
        m_PageStack.Push(m_CurrentPage);
    }

    /// <summary>
    /// Transitions back to a specified page
    /// </summary>
    /// <param name="page">Page to transition to</param>
    protected void SafeBack(IMainMenuPage page)
    {
        DeactivateCurrentPage();
        ActivatePage(page);
    }

    /// <summary>
    /// Goes back one page if any exists
    /// </summary>
    /// <param name="page">Previous page</param>
    public virtual void Back(IMainMenuPage page)
    {
        int count = m_PageStack.Count;
        if (count == 0)
        {
            SafeBack(page);
            return;
        }

        for (int i = count - 1; i >= 0; i--)
        {
            IMainMenuPage currentPage = m_PageStack.Pop();
            if (currentPage == page)
            {
                SafeBack(page);
                return;
            }
        }

        SafeBack(page);

    }

}
