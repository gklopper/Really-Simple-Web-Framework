package simpleweb;

import org.junit.Test;

import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class DispatchFilterTest {



    @Test public void shouldDispatch() {
        DispatchFilter filter = new DispatchFilter() {
            @Override
            public void configueControllers() {
                Controller testController = new TestController();

                Converter dateConverter = new DateConverter("dd-MM-yyyy", Locale.UK);

                POST("/admin/save").invokes("save").on(testController).withConverter(Date.class, dateConverter);
                GET("/admin/{id}/edit").invokes("edit").on(testController);
                GET("/admin/*").invokes("list").on(testController);
            }
        };
    }

    public class TestController extends Controller {

        public String save(@Param("id") int id,
                           @Param("name") String name,
                           @Param("date") Date date) {

            return redirect("/admin/list");
        }

        public String list() {
            addToModel("items", Collections.emptyList());
            return "list.jsp";
        }

        public String edit(@Param("id") int id) {

            return "edit.jsp";
        }
    }
}
