package edu.gatech.seclass;

public class MyString implements MyStringInterface{

    String string = null;

    @Override
    public String getString() {
        if(string.isEmpty())
            return null;
        else
            return string;
    }

    @Override
    public void setString(String string) {
        if(string.equals(easterEgg))
            throw new IllegalArgumentException();
        else
            this.string = string;
    }

    @Override
    public int countNumbers() {
        int count = 0;

        if(string.isEmpty())
            return count;
        if(string.equals(null))
            throw new NullPointerException();

        for(int i = 0; i < string.length(); i++){
            //The first digit character
            if(Character.isDigit(string.charAt(i))){
                //The following digit character(s)
                while(i < string.length()-1 && Character.isDigit(string.charAt(i+1)))
                    i++;
                count++;
            }
        }
        return count;
    }

    @Override
    public String addNumber(int n, boolean invert) {
        if(string.equals(null))
            throw new NullPointerException();
        else if(n < 0)
            throw new IllegalArgumentException();

        String convertString = "";
        int endPoint = string.length();
        for(int i = 0; i < endPoint; i++){
            int num = 0;        //value of sequential digit(s)
            int length = 0;     //the amount of sequential digits(s)

            if(Character.isDigit(string.charAt(i))){
                num+=Character.getNumericValue(string.charAt(i));
                while(i < string.length()-1 && Character.isDigit(string.charAt(i+1))) {
                    num*=10;
                    num+=Character.getNumericValue(string.charAt(i+1));
                    i++;        //skip digit(s) for next loop
                    length++;
                }
                num+=n;
            }

            if(invert && num > 0) {
                String reverseNum = new StringBuilder(String.valueOf(num)).reverse().toString();
                convertString = string.substring(0,i-length) + reverseNum + string.substring(i+1);
                string = convertString;
            }else if (num > 0){
                convertString = string.substring(0,i-length) + String.valueOf(num) + string.substring(i+1);
                string = convertString;
            }
        }

        if(convertString.isEmpty())
            return string;
        else
            return convertString;
    }

    @Override
    public void convertDigitsToNamesInSubstring(int initialPosition, int finalPosition) {
        if(string.equals(null))
            throw new NullPointerException();
        else if(initialPosition < 1 || initialPosition > finalPosition )
            throw new IllegalArgumentException();
        else if(finalPosition > string.length())
            throw new MyIndexOutOfBoundsException();

        String convertString;
        for(int i = initialPosition-1; i < finalPosition; i++){
            if(Character.isDigit(string.charAt(i))){
                char num = string.charAt(i);
                String numName = "";
                switch(num) {
                    case '0':
                        numName = "zero"; finalPosition+=3; break;
                    case '1':
                        numName = "one"; finalPosition+=2; break;
                    case '2':
                        numName = "two"; finalPosition+=2; break;
                    case '3':
                        numName = "three"; finalPosition+=4; break;
                    case '4':
                        numName = "four"; finalPosition+=3; break;
                    case '5':
                        numName = "five"; finalPosition+=3; break;
                    case '6':
                        numName = "six"; finalPosition+=2; break;
                    case '7':
                        numName = "seven"; finalPosition+=4; break;
                    case '8':
                        numName = "eight"; finalPosition+=4; break;
                    case '9':
                        numName = "nine"; finalPosition+=3; break;
                }
                convertString = string.substring(0,i) + numName + string.substring(i+1);
                string = convertString;
            }
        }
    }
}
