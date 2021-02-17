using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class GemOfEyrie : Loot
{
    /// <summary>
    /// Used to check if the player has picked up the stone
    /// An event could have been triggered for this, but time
    /// </summary>
    [HideInInspector]public bool Collected = false;

    protected override void TryPickup()
    {
        base.TryPickup();
        Collected = true;
    }
}
