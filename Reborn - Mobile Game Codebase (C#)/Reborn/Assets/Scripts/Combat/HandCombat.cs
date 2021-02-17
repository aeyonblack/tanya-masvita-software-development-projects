using System.Collections;
using System.Collections.Generic;
using UnityEngine;

/// <summary>
/// Defines the hand combat attack of the enemy
/// </summary>
[CreateAssetMenu(fileName = "HandCombat", menuName = "Reborn/Attack/Hand Combat", order = 30)]
public class HandCombat : AttackConfiguration
{
    public void ExecuteAttack(EnemyController attacker, CharacterData target)
    {
        Debug.Log("[HandCombat] Ready to execute attack");
        if (target == null)
            return;

        if (Vector3.Distance(attacker.transform.position, target.transform.position) > Range)
            return;

        if (!IsFacingTarget(attacker.transform, target.transform))
            return;

        Debug.Log("[HandCombat] Attack executing");
        var attack = CreateAttack(attacker.Stats, target.Stats);
        var attackables = target.GetComponentsInChildren(typeof(IAttackable));
        foreach (IAttackable a in attackables)
        {
            a.OnAttack(attacker, attack);
        }
    }
}
