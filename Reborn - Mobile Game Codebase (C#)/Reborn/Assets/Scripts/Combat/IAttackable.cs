using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

public interface IAttackable
{
    void OnAttack(EnemyController attacker, Attack attack);
}
