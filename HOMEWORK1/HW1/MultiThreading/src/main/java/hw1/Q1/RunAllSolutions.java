package hw1.Q1;

import java.io.IOException;

public class RunAllSolutions {
		
		public static void main(String args[]) throws IOException, InterruptedException
		{
			System.out.println("\n\n\nRunning B.1: Sequential Run");
			SolutionB1.main(args);
			System.out.println("\n\n\nRunning B.2: No Locking Run");
			SolutionB2.main(args);
			System.out.println("\n\n\nRunning B.3: Coarse Lock Run");
			SolutionB3.main(args);
			System.out.println("\n\n\nRunning B.4: Fine Lock Run");
			SolutionB4.main(args);
			System.out.println("\n\n\nRunning B.5: No Share Run");
			SolutionB5.main(args);
			System.out.println("\n\n\nRunning C.1: Sequential Run");
			SolutionC1.main(args);
			System.out.println("\n\n\nRunning C.2: No Locking Run");
			SolutionC2.main(args);
			System.out.println("\n\n\nRunning C.3: Coarse Lock Run");
			SolutionC3.main(args);
			System.out.println("\n\n\nRunning C.4: Fine Lock Run");
			SolutionC4.main(args);
			System.out.println("\n\n\nRunning C.5: No Share Run");
			SolutionC5.main(args);
			System.out.println("***End of Program***");
	
		}
}