using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using UnityEngine;
using Random = UnityEngine.Random;

public class MapBrain : MonoBehaviour
{
    // Parámetros del Algoritmo Genético.
    [SerializeField, Range(20, 100)]
    private int populationSize = 20;

    [SerializeField, Range(0, 100)]
    private int crossoverRate = 100;
    private double crossoverRatePercent;

    [SerializeField, Range(0, 100)]
    private int mutationRate = 0;
    private double mutationRatePercent;

    [SerializeField, Range(1, 100)]
    private int generationLimit = 10;

    // Variables del Algoritmo
    private List<CandidateMap> currentGeneration;
    private int totalFitnessThisGeneration, bestFitnessScoreAllTime = 0;
    public CandidateMap bestMap = null;
    private int bestMapGenerationNumber = 0, generationNumber = 1;

    // Parámetros de Fitness
    [SerializeField]
    private int fitnessCornerMin = 6, fitnessCornerMax = 9;

    [SerializeField, Range(1, 3)]
    private int fitnessCornerWeight = 1, fitnessNearCornerWeight = 1;
    [SerializeField, Range(1, 5)]
    private int fitnessPathWeight = 1;
    [SerializeField, Range(0.3f, 1f)]
    private float fitnessObstacleWeight = 1f;

    // Parámetros de Inicialización de Mapa
    [SerializeField, Range(3, 20)]
    private int widthOfMap = 11, lengthOfMap = 11;
    private Vector3 startPosition, exitPosition;
    private MapGrid grid;
    [SerializeField]
    private Direction startPositionEdge = Direction.Left, exitPositionEdge = Direction.Right;
    [SerializeField]
    private bool randomStartAndEnd = false;
    [SerializeField, Range(4, 9)]
    private int numberOfKnightPieces = 7;

    // Parámetros de visualización
    public MapVisualizer mapVisualizer;
    DateTime startDate, endDate;
    Transform parent;
    private bool isAlgorithmRunning = false;

    public Animator animator;

    public bool IsAlgorithmRunning { get => isAlgorithmRunning; }
    private void Start()
    {
        mutationRatePercent = mutationRate / 100D;
        crossoverRatePercent = crossoverRate / 100D;
        RunAlgorithm();
    }

    public void RunAlgorithm()
    {
        UiController.instance.ResetScreen();
        ResetAlgorithmVariables();
        mapVisualizer.ClearMap();

        grid = new MapGrid(widthOfMap, lengthOfMap);

        MapHelper.RandomlyChooseAndSetStartAndExit(grid, ref startPosition, ref exitPosition, randomStartAndEnd, startPositionEdge, exitPositionEdge);

        isAlgorithmRunning = true;
        animator.SetBool("AlgorithmIsRunning", true);
        startDate = DateTime.Now;
        FindOptimalSolution(grid);
    }

    private void ResetAlgorithmVariables()
    {
        totalFitnessThisGeneration = 0;
        bestFitnessScoreAllTime = 0;
        bestMap = null;
        bestMapGenerationNumber = 0;
        generationNumber = 0;
    }

    private void FindOptimalSolution(MapGrid grid)
    {
        currentGeneration = new List<CandidateMap>(populationSize);
        for (int i = 0; i < populationSize; i++)
        {
            var candidateMap = new CandidateMap(grid, numberOfKnightPieces);
            candidateMap.CreateMap(startPosition, exitPosition, true);
            currentGeneration.Add(candidateMap);
        }

        StartCoroutine(GeneticAlgorithm());
    }

    private IEnumerator GeneticAlgorithm()
    {
        totalFitnessThisGeneration = 0;
        int bestFitnessScoreThisGeneration = 0;
        CandidateMap bestMapThisGeneration = null;
        foreach (var candidate in currentGeneration)
        {
            candidate.FindPath();
            candidate.Repair();
            var fitness = CalculateFitness(candidate.ReturnMapData());

            totalFitnessThisGeneration += fitness;
            if (fitness > bestFitnessScoreThisGeneration)
            {
                bestFitnessScoreThisGeneration = fitness;
                bestMapThisGeneration = candidate;
            }

        }

        if (bestFitnessScoreThisGeneration > bestFitnessScoreAllTime)
        {
            bestFitnessScoreAllTime = bestFitnessScoreThisGeneration;
            bestMap = bestMapThisGeneration.DeepClone();
            bestMapGenerationNumber = generationNumber;
        }

        generationNumber++;
        yield return new WaitForEndOfFrame();
        UiController.instance.SetLoadingValue(generationNumber / (float)generationLimit);

        Debug.Log("Current generation " + generationNumber + " score: " + bestMapThisGeneration);

        if (generationNumber < generationLimit)
        {
            List<CandidateMap> nextGeneration = new List<CandidateMap>();

            while (nextGeneration.Count < populationSize)
            {
                var parent1 = currentGeneration[RouletteWheelSelection()];
                var parent2 = currentGeneration[RouletteWheelSelection()];

                CandidateMap child1, child2;

                CrossOverParrents(parent1, parent2, out child1, out child2);

                child1.AddMutation(mutationRatePercent);
                child2.AddMutation(mutationRatePercent);

                nextGeneration.Add(child1);
                nextGeneration.Add(child2);
            }
            currentGeneration = nextGeneration;

            StartCoroutine(GeneticAlgorithm());
        }
        else
        {
            ShowResults();
        }
    }

    private void ShowResults()
    {
        isAlgorithmRunning = false;
        animator.SetBool("AlgorithmIsRunning", false);

        Debug.Log("Best solution at generation " + bestMapGenerationNumber + " with score: " + bestFitnessScoreAllTime);

        var data = bestMap.ReturnMapData();

        //    // Hacer que si la posicion está ocupada por un obstáculo, eliminarlo y dejar el tile de inicio.
        int indexStartPoint = bestMap.Grid.CalculateIndexFromCoordinates(startPosition.x, startPosition.z);
        //Debug.Log(bestMap.Grid.GetCell(startPosition.x, startPosition.z).ObjectType); // -> Start
        //Debug.Log(bestMap.IsObstacleAt(indexStartPoint)); // 
        bestMap.PlaceObstacle(indexStartPoint, false);
        //Debug.Log(bestMap.IsObstacleAt(indexStartPoint)); // 
        mapVisualizer.VisualizeMap(bestMap.Grid, data, true);

        UiController.instance.HideLoadingScreen();

        Debug.Log("Path length: " + data.path);
        Debug.Log("Corners count: " + data.cornersList.Count);

        endDate = DateTime.Now;
        long elapsedTicks = endDate.Ticks - startDate.Ticks;
        TimeSpan elapsedSpan = new TimeSpan(elapsedTicks);
        Debug.Log("Time needed to run this genetic optimisation: " + elapsedSpan.TotalSeconds);
    }

    private void CrossOverParrents(CandidateMap parent1, CandidateMap parent2, out CandidateMap child1, out CandidateMap child2)
    {
        child1 = parent1.DeepClone();
        child2 = parent2.DeepClone();

        if (Random.value < crossoverRatePercent)
        {
            int numBits = parent1.ObstaclesArray.Length;

            int crossOverIndex = Random.Range(0, numBits);

            for (int i = crossOverIndex; i < numBits; i++)
            {
                child1.PlaceObstacle(i, parent2.IsObstacleAt(i));
                child2.PlaceObstacle(i, parent1.IsObstacleAt(i));
            }
        }
    }

    private int RouletteWheelSelection()
    {
        int randomValue = Random.Range(0, totalFitnessThisGeneration);
        for (int i = 0; i < populationSize; i++)
        {
            randomValue -= CalculateFitness(currentGeneration[i].ReturnMapData());
            if (randomValue <= 0)
            {
                return i;
            }
        }
        return populationSize - 1;
    }

    private int CalculateFitness(MapData mapData)
    {
        int numberOfObstacles = mapData.obstacleArray.Where(isObstacle => isObstacle).Count();
        int score = mapData.path.Count * fitnessPathWeight + (int)(numberOfObstacles * fitnessObstacleWeight);
        int cornersCount = mapData.cornersList.Count;
        if (cornersCount >= fitnessCornerMin && cornersCount <= fitnessCornerMax)
        {
            score += cornersCount * fitnessCornerWeight;
        }
        else if (cornersCount > fitnessCornerMax)
        {
            score -= fitnessCornerWeight * (cornersCount - fitnessCornerMax);
        }
        else if (cornersCount < fitnessCornerMin)
        {
            score -= fitnessCornerWeight * fitnessCornerMin;
        }
        score -= mapData.cornerNearEachOther * fitnessNearCornerWeight;
        return score;
    }
}
