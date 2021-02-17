using System.Collections;
using System.Collections.Generic;
using UnityEngine;

/// <summary>
/// Displays ammo on screen
/// </summary>
public abstract class AmmoDisplay : MonoBehaviour
{
    /// <summary>
    /// Updates ammo amount
    /// </summary>
    /// <param name="current">current ammo</param>
    /// <param name="max">maxiumum amount of ammo</param>
    public abstract void UpdateAmount(int current, int max);
}
