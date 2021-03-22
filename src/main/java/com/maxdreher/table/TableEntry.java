package com.maxdreher.table;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maxdreher.consumers.ConsumeAndSupply;
import com.maxdreher.consumers.ConsumeTwo;
import com.maxdreher.consumers.ConsumeTwoAndSupply;

import java.util.Comparator;

//TODO #6
public class TableEntry<Input> {

    private final String name;
    private Comparator<Input> comparator;
    private final ConsumeTwoAndSupply<Context, Input, ? super View> generate;
    private final int weight;


    public TableEntry(String name, Comparator<Input> comparator, ConsumeTwoAndSupply<Context, Input, ? super View> generator) {
        this(name, comparator, 1, generator);
    }

    public TableEntry(String name, Comparator<Input> comparator, int weight, ConsumeTwoAndSupply<Context, Input, ? super View> generator) {
        this.name = name;
        this.comparator = comparator;
        this.weight = weight;
        this.generate = generator;
    }

    public String getName() {
        return name;
    }

    public boolean isSortable() {
        return comparator != null;
    }

    public Comparator<Input> getComparator() {
        return comparator;
    }

    public View generate(Context context, Input input) {
        return (View) generate.consume(context, input);
    }


    public static <Input> ConsumeTwoAndSupply<Context, Input, View> textViewGenerator(ConsumeAndSupply<Input, String> toText, int marginsPixels) {
        return textViewGenerator(toText, null, marginsPixels);
    }

    public static <Input> ConsumeTwoAndSupply<Context, Input, View> textViewGenerator(ConsumeAndSupply<Input, String> toText, ConsumeTwo<TextView, Input> viewConsumer, int marginsPixels) {
        return (context, input) -> {
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            TextView textView = new TextView(context);
            textView.setText(toText.consume(input));
            textView.setGravity(Gravity.CENTER);

            if (marginsPixels != -1) {
                textView.setPadding(marginsPixels, marginsPixels, marginsPixels, marginsPixels);
            }
            if (viewConsumer != null) {
                viewConsumer.consume(textView, input);
            }

            linearLayout.addView(textView);
            return linearLayout;
        };
    }

    public int getWeight() {
        return weight;
    }
}
