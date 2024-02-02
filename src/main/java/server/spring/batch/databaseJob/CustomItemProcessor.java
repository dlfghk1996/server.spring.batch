package server.spring.batch.databaseJob;

import org.springframework.batch.item.ItemProcessor;
import server.spring.batch.basic.Coffee;
import server.spring.batch.common.domain.User;
import server.spring.batch.common.domain.UserPay;

public class CustomItemProcessor implements ItemProcessor<UserPay, User> {

    @Override
    public  User process(UserPay userPay) throws Exception {
        if(userPay.equals("")){
            User user = new User();
        }

        return  new User();
    }
}
