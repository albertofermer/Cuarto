using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;

public class Record : IComparable
{
    private readonly int puntos;
    private readonly string nombre;

   public Record(int puntos, string nombre)
    {
        this.puntos = puntos;
        this.nombre = nombre;
    }

    public int getPuntos()
    {
        return puntos;
    }

    public override string ToString()
    {
        return this.nombre + "\t\t\t\t\t\t\t\t\t\t\t\t" + this.puntos;
    }

    public int CompareTo(object x)
    {
        if (x == null) return 1;

        Record otherRecord = x as Record;
        if(otherRecord != null)
        {
            return this.puntos.CompareTo(otherRecord.puntos);
        }
        else
        {
            throw new ArgumentException("Object is not a Record!");
        }
    }
}
