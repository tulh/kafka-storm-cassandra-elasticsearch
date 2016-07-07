import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

/**
 * Created by tulh on 01/07/2016.
 */
public class SimpleApp
{
    public static void main(String args[])
    {
        String logFile = "/home/tulh/Downloads/spark-1.6.2/README.md";
        SparkConf conf = new SparkConf().setAppName("Simple Application");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> logData = sc.textFile(logFile).cache();

        long numAs = logData.filter(new Function<String, Boolean>()
        {
            public Boolean call(String v1) throws Exception
            {
                return v1.contains("a");
            }
        }).count();

        long numBs = logData.filter(new Function<String, Boolean>()
        {
            public Boolean call(String v1) throws Exception
            {
                return v1.contains("b");
            }
        }).count();

        System.out.printf("Lines with a: %s b: %s\n", numAs, numBs);
    }
}
