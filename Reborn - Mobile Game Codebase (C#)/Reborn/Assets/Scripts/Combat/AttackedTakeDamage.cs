using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class AttackedTakeDamage : MonoBehaviour, IAttackable
{
    private CharacterData player;

    private void Awake()
    {
        player = GetComponent<CharacterData>();
    }

    public void OnAttack(EnemyController attacker, Attack attack)
    {
        player.TakeDamage(attack);

        if (player.Stats.currentHealth <= 0)
        {
            var destructibles = GetComponents(typeof(IDestructible));
            foreach (IDestructible d in destructibles)
            {
                d.OnDestruction(player, attacker.Stats);
            }
        }
    }
}
