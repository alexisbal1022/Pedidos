package ec.compumax.pedidos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.login.LoginManager;

public class Pantallaf extends AppCompatActivity {

    Button btn_salirface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallaf);

        btn_salirface = (Button) findViewById(R.id.btn_salirface);

        btn_salirface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(Pantallaf.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
