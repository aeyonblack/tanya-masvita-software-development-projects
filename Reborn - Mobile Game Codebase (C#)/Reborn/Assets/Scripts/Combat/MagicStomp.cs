using System.Collections;
using System.Collections.Generic;
using UnityEngine;

/// <summary>
/// Handles the behaviour and data of a stomp attack
/// </summary>
[CreateAssetMenu(fileName = "New Stomp", menuName ="Reborn/Attack/Stomp", order = 50)]
public class MagicStomp : AttackConfiguration
{
    public float Radius;
    public float Duration;
    public GameObject EffectPrefab;

    /// <summary>
    /// Executes the stomp attack
    /// </summary>
    /// <param name="caster">The enemy that cast this attack</param>
    /// <param name="position">Position of the stomp effect</param>
    /// <param name="layer"></param>
    public void Execute(EnemyController caster, Vector3 position, int layer)
    {
        var stomp = Instantiate(EffectPrefab, position, Quaternion.identity);
        Destroy(stomp, Duration);
        var collidedObjects = Physics.OverlapSphere(position, Radius);

        foreach(var collision in collidedObjects)
        {
            var target = collision.gameObject.GetComponent<CharacterData>();

            if (target == null)
            {
                continue;
            }

            var attack = CreateAttack(caster.Stats, target.Stats);
            var attackables = target.GetComponentsInChildren(typeof(IAttackable));
            foreach(IAttackable a in attackables)
            {
                a.OnAttack(caster, attack);
            }
        }
    }

}
