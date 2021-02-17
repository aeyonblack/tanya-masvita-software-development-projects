using System.Collections;
using System.Collections.Generic;
using UnityEngine;

/// <summary>
/// Defines general attack data
/// </summary>
[CreateAssetMenu(fileName = "Attack", menuName = "Reborn/Attack/Basic Attack", order = 10)]
public class AttackConfiguration : ScriptableObject
{
    public float CoolDown;
    public float Range;
    public float MinDamage;
    public float MaxDamge;
    public float CriticalMultiplier;
    public float CriticalChance;

    private const float DOT_THRESHOLD = 0.5f;

    public Attack CreateAttack(CharacterStats attacker, CharacterStats target)
    {
        float coreDamage = attacker.baseDamage;
        coreDamage += Random.Range(MinDamage, MaxDamge);

        bool isCritical = Random.value < CriticalChance;
        if (isCritical)
        {
            coreDamage *= (CriticalMultiplier + attacker.currentAgression);
            Debug.Log("Core damage = " + coreDamage);
        }

        return new Attack((int)coreDamage, isCritical);
    }

    protected bool IsFacingTarget(Transform attacker, Transform target)
    {
        var vectorToTarget = target.position - attacker.position;
        vectorToTarget.Normalize();

        float dot = Vector3.Dot(attacker.forward, vectorToTarget);
        return dot >= DOT_THRESHOLD;
    }

}
