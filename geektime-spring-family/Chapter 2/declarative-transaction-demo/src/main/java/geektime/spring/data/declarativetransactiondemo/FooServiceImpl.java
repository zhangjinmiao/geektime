package geektime.spring.data.declarativetransactiondemo;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class FooServiceImpl implements FooService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void insertRecord() {
        jdbcTemplate.execute("INSERT INTO FOO (BAR) VALUES ('AAA')");
    }

    @Override
    @Transactional(rollbackFor = RollbackException.class)
    public void insertThenRollback() throws RollbackException {
        jdbcTemplate.execute("INSERT INTO FOO (BAR) VALUES ('BBB')");
        throw new RollbackException();
    }

    @Override
    public void invokeInsertThenRollback() throws RollbackException {
        insertThenRollback();
    }

//    第一步：注入实例
    @Autowired
    private FooService fooService;

    //把自己的实例注入进来，比较方便理解
    @Override
    public void invokeInsertThenRollbackBySelfService() throws RollbackException {
        fooService.insertThenRollback();
    }
    //获取当前代理，这样写避免了自己调用自己的实例
    @Override
    public void invokeInsertThenRollbackByAopContext() throws RollbackException {
        ((FooService) (AopContext.currentProxy())).insertThenRollback();
    }
    //再加一层事务
    @Transactional(rollbackFor = RollbackException.class)
    @Override
    public void invokeInsertThenRollbackAddTransactional() throws RollbackException {
        insertThenRollback();
    }
}
