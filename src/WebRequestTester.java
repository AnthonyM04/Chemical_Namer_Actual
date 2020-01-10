import WebRequest.URL_Requester;
import WebRequest.RequestType;

import java.io.IOException;
import java.util.Scanner;

public class WebRequestTester {
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner in = new Scanner(System.in);

        System.out.println("1: FORMULA");
        System.out.println("2: NAME");
        System.out.println("Input your formula or name.");

        while (in.hasNextLine())
            try {
                int firstInputNum = Integer.parseInt(in.nextLine());
                String secondInput = in.nextLine();
                RequestType firstInput;

                switch(firstInputNum) {
                    case(1):
                        firstInput = RequestType.FORMULA;
                        break;
                    case(2):
                        firstInput = RequestType.NAME;
                        break;
                    default:
                        throw new IllegalArgumentException();
                }

                URL_Requester url_requester = new URL_Requester(firstInput, secondInput);
                System.out.println(url_requester.readURLs());
            } catch (NumberFormatException e1) {
                System.out.println("Not an integer.");
            } catch (IllegalArgumentException e2) {
                System.out.println("Integer was not 1 or 2.");
            }

        }
    }

