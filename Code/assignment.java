public class assignment {
    public static void main(String [] args){
        int Buildings, Rooms, Course, Time, Result;
        long Seed = 0; long Limit, Remains;
        double score;
        Buildings = 0;  Rooms = 0; Course = 0; Time = 0; Result = 0;

        //Pre-caution to check before running the program
        if(args.length == 6){
            try{
                Buildings = Integer.parseInt(args[0]);
                Rooms = Integer.parseInt(args[1]);
                Course = Integer.parseInt(args[2]);
                Time = Integer.parseInt(args[3]);
                Result = Integer.parseInt(args[4]);
                Seed = Long.parseLong(args[5]);
            }catch (NumberFormatException e) {
                System.exit(1);
            }
        }else{System.exit(1);}

        //Experiment Initial:
        System.out.println("Buildings: "+Buildings+" Rooms: "+Rooms+ " Courses: "+Course);
        System.out.println("Time: "+Time+" Results: "+Result+ " Seed variable: "+Seed);

        //Experiment:
        Problem test = new Problem(Seed);
        Problem.createInstance(Buildings,Rooms,Course);

        Search src = new Search();
        Limit = (System.currentTimeMillis()) + (1000 * Time);

        Plan answer = null;

        switch(Result){
            case 0:
                answer = src.Baseline(test, Limit);
                break;
            case 1:
                answer = src.Annealing(test, Limit);
                break;
            case 2:
                answer = src.BackTracking(test, Limit);
                break;
            default:
                System.out.println("Algorithm isn't currently available");
                System.exit(1);
        }

        Remains = Limit - System.currentTimeMillis();
        System.out.println("Time Limit: "+Limit+" Current Time: "+System.currentTimeMillis()+"Remanding Time: "+Remains);

        if(System.currentTimeMillis() > Limit){System.out.println("Above time limit");}

        //Experiment Results:
        score = test.Eval_Sch(answer);
        System.out.println("Score: "+score);

    }// End of the main method
}
