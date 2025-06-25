package com.example.zencode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ContractAdapter.OnContractListener {

  private RecyclerView recyclerView;
  private ContractAdapter adapter;
  private List<ZencodeContract> contractList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    recyclerView = findViewById(R.id.recyclerViewContracts);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setHasFixedSize(true);

    // Load your contracts
    loadContracts();

    // Set up the adapter
    adapter = new ContractAdapter(contractList, this);
    recyclerView.setAdapter(adapter);
  }

  private void loadContracts() {
    contractList = new ArrayList<>();

    // Example 1: Generate Keypair
    String keypairContract = "rule check version 1.0.0\n"
            + "Scenario 'ecdh':Create the keypair\n"
            + "Given that I am known as 'Alice'\n"
            + "When I create the ecdh key\n"
            + "Then print the 'keyring'";
    contractList.add(new ZencodeContract(
            "Generate ECDH Keypair",
            keypairContract,
            "", // No keys needed
            ""  // No data needed
    ));

    // Example 2: Sign a message
//    String signContract = "rule check version 1.0.0\n"
//            + "Scenario 'sign': sign a message\n"
//            + "Given I have a 'keyring' and a 'message'\n"
//            + "When I sign the 'message' with the 'keyring'\n"
//            + "Then print the 'signature'";
//    String signKeys = "{\n"
//            + "  \"keyring\": {\n"
//            + "    \"public_key\": \"...\",\n"
//            + "    \"private_key\": \"...\"\n"
//            + "  }\n"
//            + "}";
//    String signData = "{\n"
//            + "  \"message\": \"Hello Zenroom!\"\n"
//            + "}";
//    contractList.add(new ZencodeContract(
//            "Sign a Message",
//            signContract,
//            signKeys,
//            signData
//    ));

    // Add more contracts here...
  }

  @Override
  public void onContractClick(int position) {
    // Create an intent to open the detail activity
    Intent intent = new Intent(this, ContractDetailActivity.class);
    // Pass the selected contract object to the detail activity
    intent.putExtra("SELECTED_CONTRACT", contractList.get(position));
    startActivity(intent);
  }
}