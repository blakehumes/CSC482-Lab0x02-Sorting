package com.company;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.lang.Math;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    public static void main(String[] args) {

        char[][] UnsortedArray = GenerateTestList(7,8,65,70);
        System.out.println("Unsorted List:");
        for(int i = 0; i < UnsortedArray.length; i++){
          System.out.format("%s  ", String.valueOf(UnsortedArray[i]));

        }
        System.out.format("\nIs the array sorted: %d\n", IsSorted(UnsortedArray));
        System.out.println("Radix Sort (D2):");
        //char[][] SortedArray = SelectionSort(UnsortedArray);
        //char[][] SortedArray = MergeSort(UnsortedArray);
        //QuickSortClass qs = new QuickSortClass(UnsortedArray);
        //char[][] SortedArray = qs.QuickSort(0,UnsortedArray.length - 1);
        char[][] SortedArray = RadixSort(UnsortedArray, 2);
        for(int i = 0; i < SortedArray.length; i++){
          System.out.format("%s  ", String.valueOf(SortedArray[i]));

        }
        System.out.format("\nIs the array sorted: %d\n", IsSorted(SortedArray));


        //RunTimeTests(100000000,4);


    }
    public static void RunTimeTests(int maxN, int sortType){
        // Formating the table
        System.out.format("%12s %12s %12s %12s %12s %12s %12s %12s %12s %18s\n",
                "", "k = 6", "", "k = 12", "",
                "k = 24", "", "k = 48", "", "");
        System.out.format("%12s %12s %12s %12s %12s %12s %12s %12s %12s %18s\n",
                "N", "Time", "2x Ratio", "Time", "2x Ratio",
                "Time", "2x Ratio", "Time", "2x Ratio", "Pred 2x Ratio");

        // Declaring the variables that track the times and ratios
        long StartSort = 0, EndSort = 0;
        long[] PrevK = new long[4];
        long[] KTime = new long[4];
        float[] DoubleRatio = new float[4];
        double[] PredDoublRatio = new double[5];
        PredDoublRatio[1] = 4;
        PredDoublRatio[2] = 0;
        PredDoublRatio[3] = 0;
        PredDoublRatio[4] = 2;

        // Fill arrays with zeroes to ensure no random values
        Arrays.fill(PrevK, 0);
        Arrays.fill(KTime, 0);

        int n = 1;
        int k = 6;
        long multiTime = 0;
        while(n <= maxN){
            for(int i = 0; i < 4; i++){

                // Only perform the test once if n > 1024. Else, perform the test 10 times and use the average
                if(n > 1024){

                    // Create unsorted array of strings
                    char[][] UnsortedArray = GenerateTestList(n,k,1,255);
                    char[][] SortedArray;

                    // Beginning of the test
                    StartSort = getCpuTime();

                    // Switch to determine which sort is used
                    switch(sortType) {
                        case 1:
                            SortedArray = SelectionSort(UnsortedArray);
                            break;
                        case 2:
                            SortedArray = MergeSort(UnsortedArray);
                            break;
                        case 3:
                            QuickSortClass qs = new QuickSortClass(UnsortedArray);
                            SortedArray = qs.QuickSort(0, UnsortedArray.length - 1);
                            break;
                        case 4:
                            SortedArray = RadixSort(UnsortedArray, 3);
                            break;
                    }
                    EndSort = getCpuTime();
                    // End of the Test
                }
                else{
                    // 10x Test
                    for(int j = 0; j < 10; j++){
                        char[][] UnsortedArray = GenerateTestList(n,k,1,255);
                        char[][] SortedArray;
                        StartSort = getCpuTime();
                        switch(sortType) {
                            case 1:
                                SortedArray = SelectionSort(UnsortedArray);
                                break;
                            case 2:
                                SortedArray = MergeSort(UnsortedArray);
                                break;
                            case 3:
                                QuickSortClass qs = new QuickSortClass(UnsortedArray);
                                SortedArray = qs.QuickSort(0, UnsortedArray.length - 1);
                                break;
                            case 4:
                                System.out.println(KTime[i]);
                                SortedArray = RadixSort(UnsortedArray, 3);
                                break;
                        }
                        EndSort = getCpuTime();
                        multiTime += (EndSort - StartSort)/1000; //
                    }
                }
                PrevK[i] = KTime[i];
                KTime[i] = (n > 1024)? (EndSort - StartSort)/1000: multiTime/10;
                System.out.println(KTime[i]);
                DoubleRatio[i] = (n == 1)? 0: (float)KTime[i]/PrevK[i];
                multiTime = 0;
                k = k * 2;
            }
            if(n > 1 && sortType == 2 || sortType == 3){
                PredDoublRatio[sortType] = Math.log(n)/Math.log(2) * n /(Math.log(n/2)/Math.log(2) * n/2);
            }

            System.out.format("%12s %12s %12s %12s %12s %12s %12s %12s %12s %18s\n",
                    n, KTime[0], DoubleRatio[0], KTime[1], DoubleRatio[1],
                    KTime[2], DoubleRatio[2], KTime[3], DoubleRatio[3], PredDoublRatio[sortType]);



            n = n * 2;
        }
    }

    public static char[][] RadixSort(char[][] StringArray, int d){
        int n = StringArray.length; // # of strings
        int n2 = StringArray[0].length; // width of strings

        char[][] output = new char[n][StringArray[0].length];
        int[] count = new int[(int)Math.pow(256, d)]; // used for counting and prefix sum

        // Iterate through the list to count the "digits", prefix sum the count, sort the list based on current digit j
        for(int j = 1; j < n2 / d + 1; j++) {
            Arrays.fill(count, 0);

            // "Count" how many elements are in each bucket
            for (int i = 0; i < n; i++) {
                int val = getVal(StringArray, d, i, j-1, n2-1);
                count[val]++;
            }

            // Prefix sum the count
            for (int i = 1; i < count.length; i++) {
                count[i] += count[i - 1];
            }

            // Sort based on the prefix sum, last element sorted first
            for (int i = n - 1; i >= 0; i--) {
                int val = getVal(StringArray, d, i, j-1, n2-1);
                output[count[val] - 1] = StringArray[i];
                count[val]--;
            }

            // Copy output of above for loop into the "input" array (StringArray)
            for (int i = 0; i < n; i++) {
                StringArray[i] = output[i];
            }
        }

        return StringArray;
    }

    // Loops through and sums the multiple byte digits
    public static int getVal(char[][] arr, int d, int i, int j, int n2){
        int val = 0;
        for(int k = 0; k < d; k++){
            //System.out.format("%d - %d * %d - %d\n", n2, d, j, k);
            val += arr[i][n2 - d * j - k] *  (int)Math.pow(2, 8 * k);
        }
        //System.out.println("-");
        return val;
    }

    static class QuickSortClass{
        char[][] quickArray; // Array to be sorted

        public char[][] QuickSort(int lower, int upper){
            if(lower < upper){
                // sort around mid or "pivot"
                int mid = SortMid(lower, upper);
                QuickSort(lower, mid - 1);
                QuickSort(mid + 1, upper);
            }
            return quickArray;
        }
        public int SortMid(int lower, int upper){
            int i = lower - 1; // Tracks the last element swapped to the left of the "mid" element
            char[] p = quickArray[upper]; // Value of mid/pivot

            // Iterate from lower to upper element in array, sorting elements less than pivot into lower side of array
            // Had some issues figuring out the best way track what needed to be swapped. Found help here: https://www.geeksforgeeks.org/quick-sort/
            for(int j = lower; j < upper; j++){
                if(String.valueOf(p).compareTo(String.valueOf(quickArray[j])) > 0){
                    i++;

                    // Swap element that is less than pivot into lower side of array
                    char[] temp = quickArray[j];
                    quickArray[j] = quickArray[i];
                    quickArray[i] = temp;
                }
            }

            // Swap the pivot (upper) into the pivot index (i + 1)
            char[] temp = quickArray[i + 1];
            quickArray[i + 1] = quickArray[upper];
            quickArray[upper] = temp;
            return i + 1;
        }

        // Constructor to fill in array
        QuickSortClass(char[][] arr){
            this.quickArray = arr;
        }
    }

    public static char[][] MergeSort(char[][] StringArray){
        int n = StringArray.length;

        // Arrays of less than 2 elements don't need sorted. Return array as is.
        if(n < 2)
            return StringArray;
        int n2 = StringArray[0].length;

        int mid = n / 2; // Mid point of array
        char[][] left = new char[mid][n2]; // Create left array for 1st half of StringArray
        char[][] right = new char[n - mid][n2]; // Create right array for 2nd half of StringArray

        // Fill left and right array with 1st and 2nd half of String Array, respectively.
        for(int i = 0; i < mid; i++){
            left[i] = StringArray[i];
        }
        for(int j = 0; j < n - mid; j++){
            right[j] = StringArray[mid + j];
        }

        // Let the recursion commence!
        MergeSort(left);
        MergeSort(right);

        // Merge left and right array into StringArray
        return Merge(StringArray, left, right);

    }

    public static char[][] Merge(char[][] StringArray, char[][] left, char[][] right){
        int l = left.length;
        int r = right.length;
        int i = 0; //index for left array
        int j = 0; //index for right array
        int k = 0; //index for main array

        // Compare right and left arrays until at least one array is empty
        while(i < l && j < r){
            // Sorts the lesser of the current elements of left and right arrays into StringArray
            // If current element of left array is lesser or equal to current element in right array, sort left element into StringArray and increment index values
            // Else sort right element into StringArray and increment index values
            if(String.valueOf(left[i]).compareTo(String.valueOf(right[j])) <= 0){
                StringArray[k++] = left[i++];
                //Found the post increment trick here: https://www.baeldung.com/java-merge-sort
            }
            else{
                StringArray[k++] = right[j++];
            }
        }

        // After at least 1 array is empty, copy remaining elements of the other array into StringArray
        while(i < l){
            StringArray[k++] = left[i++];
        }
        while(j < r){
            StringArray[k++] = right[j++];
        }

        return StringArray;
    }

    public static int IsSorted(char[][] StringArray){
        //Returns true if array is 0 or 1 elements in length
        if(StringArray.length < 2) return 1;

        for(int i = 0; i < StringArray.length - 1; i++){
            //Checks if element i is greater than element i+1 and returns false
            if(String.valueOf(StringArray[i]).compareTo(String.valueOf(StringArray[i+1])) > 0)
                return -1;
        }
        //If no element pair in the above for loop returned false, array is sorted. Return true.
        return 1;
    }

    public static char[][] SelectionSort(char[][] StringArray){
        int mindex; // index of the min value during inner loop
        int i,j; //loop variables later used for array index


        for(i = 0; i < StringArray.length - 1; i++){
            mindex = i;

            // Iterate through the array sublist and identify the index of the lowest value
            for(j = i + 1; j < StringArray.length; j++){

                    mindex = j;

                if(String.valueOf(StringArray[mindex]).compareTo(String.valueOf(StringArray[j])) > 0 ){


                    mindex = j;
                }
            }
            //Switch the index of the "minimum" string in the latter part of the array with the element at index i
            char[] tempArray = StringArray[i];
            StringArray[i] = StringArray[mindex];
            StringArray[mindex] = tempArray;

        }
        return StringArray;
    }

    public static char[][] GenerateTestList(int N, int k, int minV, int maxV){

        char[][] StringArray = new char[N][k+1];

        //Loops through multidimensional array and fills with random char or null terminator
        for(int i = 0; i < N; i++){
            for(int j = 0; j < k+1; j++){
                if(j == k){
                    //Fill null terminator at end of "string"
                    StringArray[i][j] = 0;
                }
                else{
                    //Fill array with random char between minV and maxV
                    //Used top comment for random number generator https://stackoverflow.com/questions/363681/how-do-i-generate-random-integers-within-a-specific-range-in-java
                    StringArray[i][j] = (char)ThreadLocalRandom.current().nextInt(minV, maxV);
                }
            }
        }
        return StringArray;
    }
    /** Get CPU time in nanoseconds since the program(thread) started. */
    /** from: http://nadeausoftware.com/articles/2008/03/java_tip_how_get_cpu_and_user_time_benchmarking#TimingasinglethreadedtaskusingCPUsystemandusertime **/
    public static long getCpuTime( ) {

        ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

        return bean.isCurrentThreadCpuTimeSupported( ) ?

                bean.getCurrentThreadCpuTime( ) : 0L;

    }
}

// Code for testing
/*char[][] UnsortedArray = GenerateTestList(7,8,65,70);
        //System.out.println("Unsorted List:");
        //for(int i = 0; i < UnsortedArray.length; i++){
          //  System.out.format("%s  ", String.valueOf(UnsortedArray[i]));

        //}
        System.out.format("\nIs the array sorted: %d\n", IsSorted(UnsortedArray));
        System.out.println("Radix Sort:");
        //char[][] SortedArray = SelectionSort(UnsortedArray);
        //char[][] SortedArray = MergeSort(UnsortedArray);
        //QuickSortClass qs = new QuickSortClass(UnsortedArray);
        //char[][] SortedArray = qs.QuickSort(0,UnsortedArray.length - 1);
        char[][] SortedArray = RadixSort(UnsortedArray, 1);
        //for(int i = 0; i < SortedArray.length; i++){
          //  System.out.format("%s  ", String.valueOf(SortedArray[i]));

        //}
        System.out.format("\nIs the array sorted: %d\n", IsSorted(SortedArray));*/
/*char[][] UnsortedArray = GenerateTestList(10000,16,65,70);
        //System.out.println("Unsorted List:");
        //for(int i = 0; i < UnsortedArray.length; i++){
          //  System.out.format("%s  ", String.valueOf(UnsortedArray[i]));

        //}
        System.out.format("Synthesizing array of length %d, with key width of %d and digit length of %d....\n", 10000, 16, 2);
        System.out.println("Sorting via the RADIX SORT process....");
        //char[][] SortedArray = SelectionSort(UnsortedArray);
        //char[][] SortedArray = MergeSort(UnsortedArray);
        //QuickSortClass qs = new QuickSortClass(UnsortedArray);
        //char[][] SortedArray = qs.QuickSort(0,UnsortedArray.length - 1);
        char[][] SortedArray = RadixSort(UnsortedArray, 2);
        //for(int i = 0; i < SortedArray.length; i++){
          //  System.out.format("%s  ", String.valueOf(SortedArray[i]));

        //}
        String result;
        if(IsSorted(SortedArray) == 1)
            result = "Sorted";
        else
            result = "Unsorted";
        System.out.format("Verifying results.... %s!\n",result);*/