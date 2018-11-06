import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class MD5Bruteforce {

    public static void main(String[] argsd){

    }

    String psw;

    public MD5Bruteforce(String MD5){
        boolean havePassword = false;
        int i = 0;
        while(havePassword == false){



            String password = String.format("%04d", i);

            System.out.print(password + " : ");

            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            byte[] hashInBytes = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashInBytes) {
                sb.append(String.format("%02x", b));
            }

            System.out.println(sb);
            if(MD5.equals(sb.toString())){
                psw = password;
                havePassword = true;
            }

            i++;
        }
    }


    public String getPassword(){
        return psw;
    }

}