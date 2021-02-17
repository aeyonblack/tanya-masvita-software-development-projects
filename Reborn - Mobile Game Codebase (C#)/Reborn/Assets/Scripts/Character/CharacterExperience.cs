using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

/// <summary>
/// Calculates player experience when player kills enemy or collects loot
/// Player level increases depending on this experience. 
/// The Experience is also displayed in the User Interface as numbers and 
/// graphical elements.
/// </summary>
public class CharacterExperience
{
    private CharacterData m_Player;

    // Stores the amount of xp required to reach level where level = array index
    public static readonly int[] ExperiencePerLevel = { 0, 650, 1525, 2820, 4535, 6670, 17830, 33370, 54010, 87085,
        128335, 183197, 258208, 351779, 473217, 1032283 };

    // Invoked when the player changes level
    public event Action LevelChanged;

    public void Initialize(CharacterData player)
    {
        m_Player = player;
    }

    /// <summary>
    /// Add experience 
    /// </summary>
    /// <param name="amount"></param>
    public void AddExperience(int amount)
    {
        m_Player.Stats.experiencePoints += amount;
        LevelManager.instance.LevelStats.AddXp(amount);
        while (m_Player.Stats.experiencePoints >= ExperiencePerLevel[m_Player.Stats.currentLevel+1])
        {
            if (IsMaxLevel())
                break;
            LevelUp();
        }
        Debug.Log("Experience Updated, XP = " + m_Player.Stats.experiencePoints);
    }

    /// <summary>
    /// Check whether the player has reached the maximum level possible
    /// </summary>
    /// <returns>True if player is currently at max level. False otherwise</returns>
    private bool IsMaxLevel()
    {
        if (m_Player.Stats.currentLevel == m_Player.Stats.maxLevel)
        {
            return true;
        }
        return false;
    }

    private void LevelUp()
    {
        m_Player.Stats.currentLevel++;
        Controller.instance.AudioSource.PlayOneShot(m_Player.LevelUpSound);
    }

    /// <summary>
    /// Get the amount of xp required for the player to reach the next level
    /// </summary>
    /// <returns></returns>
    public int GetExperienceToNextLevel()
    {
        Debug.Log("[GetExperienceToNextLevel]" + ExperiencePerLevel[m_Player.Stats.currentLevel + 1]);
        return ExperiencePerLevel[m_Player.Stats.currentLevel + 1]; 
    }
}
