using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class GameManager : MonoBehaviour
{
    public GameObject[] hazards;
    public Vector3 spawnValues;
    public int hazardCount;
    public float spawnWait;
    public float startWait;
    public float waveWait;

    // User Interface
    public Text scoreText;
    public Text killsText;
    public Canvas endGameCanvas; 

    // Game Variables
    private int score;
    private int kills;
    private bool gameOver;

   

    // Start is called before the first frame update
    void Start()
    {
        gameOver = false;
        endGameCanvas.enabled = false;
        score = 0;
        UpdateScore();
        StartCoroutine(SpawnWaves());
    }



    // Main Game Mechanism here
    IEnumerator SpawnWaves()
    {
        yield return new WaitForSeconds(startWait);
        while(true)
        {
            for (int i = 0; i < hazardCount; i++)
            {
                GameObject hazard = hazards[Random.Range(0, hazards.Length)];
                Vector3 spawnPosition = new Vector3(Random.Range(-spawnValues.x, spawnValues.x), spawnValues.y, spawnValues.z);
                Quaternion spawnRotation = Quaternion.identity;
                Instantiate(hazard, spawnPosition, spawnRotation);
                yield return new WaitForSeconds(spawnWait);
            }

            yield return new WaitForSeconds(waveWait);

            if (gameOver)
            {
                break;
            }
        }
    }

    // Update is called once per frame
    void Update()
    {
        // Do nothing for now 
    }

    private void UpdateScore()
    {
        scoreText.text = score.ToString();
    }

    public void AddScore(int newScoreValue)
    {
        // For every score added, there is one kill
        score += newScoreValue;
        AddKill();
        UpdateScore();
    }

    private void UpdateKills()
    {
        killsText.text = kills.ToString();
    }

    private void AddKill()
    {
        kills += 1;
        UpdateKills();
    }

    // Show game over canvas...
    public void GameOver()
    {
        endGameCanvas.enabled = true; // Function working properly
        gameOver = true;
    }

}
