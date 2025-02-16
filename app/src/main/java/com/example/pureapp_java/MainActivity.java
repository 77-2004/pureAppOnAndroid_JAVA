package com.example.pureapp_java;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.pureapp_java.databinding.ActivityMainBinding;
import com.iflytek.sparkchain.core.LLM;
import com.iflytek.sparkchain.core.LLMConfig;
import com.iflytek.sparkchain.core.LLMFactory;
import com.iflytek.sparkchain.core.LLMOutput;
import com.iflytek.sparkchain.core.SparkChain;
import com.iflytek.sparkchain.core.SparkChainConfig;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    String TAG = "pureTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "start");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        Log.i(TAG,"开始配置");

        try {
            // 配置应用信息
            SparkChainConfig config = SparkChainConfig.builder()
                    .appID("$2b5477f7")
                    .apiKey("$066129f82388e7b15481a92d685b9d92")
                    .apiSecret("$YTJjNDQ0ZjY3NTUwNmZmNDM3MTk2MmVm");
            Log.i(TAG,"完成配置");
            // 初始化 SDK
            int ret = SparkChain.getInst().init(getApplicationContext(), config);
            if (ret != 0) {
                Log.e(TAG, "配置err: " + ret);
            } else {
                Log.i(TAG, "应用信息配置成功");
            }
        } catch (Exception e) {
            Log.e(TAG, "初始化过程中发生异常: " + e.getMessage());
            e.printStackTrace();
        }


        // 创建 LLMConfig 配置
        LLMConfig llmConfig_set = LLMConfig.builder()
                .domain("max-32k") // 设置领域
                .url("wss://spark-api.xf-yun.com/chat/max-32k")
                .maxToken(50); // 设置最大 token 长度

        LLM chat_llm = LLMFactory.textGeneration(llmConfig_set);
        Log.i(TAG,"LLMConfig创建成功");

        // 同步调用
        String question = "给我讲个笑话吧。";
        LLMOutput syncOutput = chat_llm.run(question); // 使用默认超时时间
        Log.i(TAG, "成功调用");

        // 解析获取的结果
        String content = syncOutput.getContent(); // 获取调用结果
        String syncRaw = syncOutput.getRaw(); // 获取星火原始回复
        int errCode = syncOutput.getErrCode(); // 获取结果 ID, 0: 调用成功，非 0: 调用失败
        String errMsg = syncOutput.getErrMsg(); // 获取错误信息
        String role = syncOutput.getRole(); // 获取角色信息
        String sid = syncOutput.getSid(); // 获取本次交互的 sid
        int completionTokens = syncOutput.getCompletionTokens(); // 获取回答的 Token 大小
        int promptTokens = syncOutput.getPromptTokens(); // 获取包含历史问题的总 Token 大小
        int totalTokens = syncOutput.getTotalTokens(); // 获取本次交互的总 Token 大小

        Log.e("pureTag","错误信息"+errMsg);

        // 6. 打印结果
        Log.i(TAG, "回复内容" + content);

    }

}