package com.example.zencode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

    Toolbar toolbar = findViewById(R.id.toolbar_main);
    setSupportActionBar(toolbar);
    // Optional: set a title. If you only want the logo, you can leave it blank.
    getSupportActionBar().setTitle(" Zencode Contracts");

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

    String signContract = "# Here we're loading the data needed for the 'find the needle in the haystack' script\n" +
            "# Use that when you want to process an array and you need to see if a 'quarum' of items in it\n" +
            "# have a certain value.\n" +
            "Given I have a 'string array' named 'haystack'\n" +
            "Given I have a 'number' named 'quorum'\n" +
            "Given I have a 'string' named 'needle'\n" +
            "\n" +
            "# Here we're loading an array that we'll hash and manipulate\n" +
            "Given I have a 'string array' named 'myArrayToBeHashed'\n" +
            "Given I have a 'string' named 'myString'\n" +
            "\n" +
            "# Copy one array element into a simple object, and rename it\n" +
            "When I create the copy of element '3' from array 'myArrayToBeHashed'\n" +
            "When I rename the 'copy' to 'RingosEmail'\n" +
            "\n" +
            "# We can also copy a simple object like a string:\n" +
            "When I copy 'myString' to 'myFavouriteCity'\n" +
            "\n" +
            "# Here we check if a certain string can be found in an array or not\n" +
            "# The statement (and the following one) can be used as conditions\n" +
            "When I verify the 'RingosEmail' is found in 'myArrayToBeHashed'\n" +
            "When I verify the 'myString' is not found in 'myArrayToBeHashed'\n" +
            "\n" +
            "# Here we make things more interesting: we check if the content of the string named 'needle' is found at least 5 times in the 'haystack'\n" +
            "When I verify the 'needle' is found in 'haystack' at least 'quorum' times\n" +
            "\n" +
            "# He're we are Hashing an array using sha256\n" +
            "When I create the hash of 'myArrayToBeHashed' using 'sha256'\n" +
            "When I rename the 'hash' to 'mysha256HashedArray'\n" +
            "\n" +
            "# Insert the value of a simple object into an array\n" +
            "When I move 'myString' in 'myArrayToBeHashed'\n" +
            "\n" +
            "# Here we are printing out just some of the stuff we're using\n" +
            "Then print 'mysha256HashedArray'\n" +
            "Then print 'RingosEmail'\n" +
            "Then print 'myArrayToBeHashed'\n" +
            "Then print 'myFavouriteCity'\n";
    String signKeys = "{\n"
            + "    \"quorum\": 5,\n"
            + "    \"needle\": \"Approved\"\n"
            + "}";
    String signData = "{\n" +
            "    \"myArrayToBeHashed\": [\n" +
            "        \"john@beatles.com\",\n" +
            "        \"paul@beatles.com\",\n" +
            "        \"ringo@beatles.com\",\n" +
            "        \"george@beatles.com\"\n" +
            "    ],\n" +
            "    \"myString\": \"I love Liverpool!\",\n" +
            "    \"haystack\": [\n" +
            "        \"Approved\",\n" +
            "        \"Not approved\",\n" +
            "        \"Approved\",\n" +
            "        \"junk data blah blah 123456\",\n" +
            "        \"Approved\",\n" +
            "        12345678,\n" +
            "        \"Approved\",\n" +
            "        {\n" +
            "            \"stuff\": \"blah blah blah\"\n" +
            "        },\n" +
            "        \"Approved\",\n" +
            "        [\n" +
            "            \"some more junk\"\n" +
            "        ]\n" +
            "    ]\n" +
            "}";
    contractList.add(new ZencodeContract(
            "Data Manipulation",
            signContract,
            signKeys,
            signData
    ));

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