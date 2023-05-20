using UnityEngine;
using System.IO;
public class HandleText : MonoBehaviour
{
    public static void WriteString(string file, string text, bool append)
    {
        string path = Application.persistentDataPath + "/" + file;
        //Write some text to the test.txt file
        StreamWriter writer = new StreamWriter(path, append);
        writer.WriteLine(text);
        writer.Close();
        StreamReader reader = new StreamReader(path);
        //Print the text from the file
        Debug.Log(Application.persistentDataPath + "/" + file);
        reader.Close();
    }
    public static string ReadString(string file)
    {
        string path = Application.persistentDataPath + "/" + file;
        //Read the text from directly from the test.txt file
        StreamReader reader = new(path);

        string str = reader.ReadToEnd();
        reader.Close();
        return str;
    }

    public static int NumberLines(string file)
    {
        string path = Application.persistentDataPath + "/" + file;
        using (StreamReader r = new StreamReader(path))
        {
            int i = 0;
            while (r.ReadLine() != null) { i++; }
            r.Close();
            return i;
        }
    }

    public static string ReadLine(int numero, string file)
    {
        string path = Application.persistentDataPath + "/" + file;
        StreamReader r = new StreamReader(path);
        for (int line = 0; line < numero; line++)
        {
            r.ReadLine();
        }
        string lastLine = r.ReadLine();
        r.Close();
        return lastLine;
    }
}