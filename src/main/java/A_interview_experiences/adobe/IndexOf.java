package A_interview_experiences.adobe;

public class IndexOf {
    public static int indexOf(String text, String pat){
        for (int i = 0; i < text.length(); i++) {
            int ii=i;
            int j=0;
            while(j < pat.length() && pat.charAt(j) == text.charAt(ii)) {
                ii++; j++;
            }
            if (j == pat.length())
                return i;
        }
        return -1;
    }


    public static int kmpSearch(String text, String pat){
        int n =  text.length(), m = pat.length();
        int[] lsp = computeLsp(pat);

        int i = 0; // text pointer
        int j = 0; // pat pointer

        while (i < n){
            if (text.charAt(i) == pat.charAt(j)){
                i++;
                j++;
            }

            // check if found a math
            if (j == m)
                return i-j;
            else{ //mismatch
                if (j != 0)
                    j = lsp[j-1]; // jump using lsp, skip already match prefix
                else
                    i++;
            }
        }
        return -1;
    }

    // lsp -> longest suffix which is also a prefix --> tells length of the character we already matched and can skip
    public static int[] computeLsp(String pat){
        int m = pat.length();
        int[] lsp = new int[m];
        int len = 0, i = 1;
        while(i < m){
            if (pat.charAt(i) == pat.charAt(len)){
                len++;
                lsp[i] = len;
                i++;
            }else{
                if (len != 0)
                    len = lsp[len] -1;
                else {
                    lsp[i] = 0;
                    i++;
                }
            }
        }
        return lsp;
    }
}
