package com.example.philatelia.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.philatelia.R;
import com.example.philatelia.adapters.CartAdapter;
import com.example.philatelia.data.CartItemEntity;
import com.example.philatelia.viewmodels.CartViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class CartFragment extends Fragment implements CartAdapter.CartActionListener {
    private CartViewModel viewModel;
    private CartAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayout emptyPlaceholder;
    private TextView totalSumView;
    private MaterialButton checkoutButton;
    private String lastTotalRub = "0.00";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.cart_recycler_view);
        emptyPlaceholder = view.findViewById(R.id.empty_placeholder);
        totalSumView = view.findViewById(R.id.total_sum);
        checkoutButton = view.findViewById(R.id.checkout_button);

        adapter = new CartAdapter(new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        viewModel.migrateLegacyCartIfNeeded();

        viewModel.getCartItems().observe(getViewLifecycleOwner(), items -> {
            adapter.setItems(items);
            if (items == null || items.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyPlaceholder.setVisibility(View.VISIBLE);
                checkoutButton.setEnabled(false);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyPlaceholder.setVisibility(View.GONE);
                checkoutButton.setEnabled(true);
            }
        });
        viewModel.getTotalSum().observe(getViewLifecycleOwner(), sum -> {
            if (sum != null) {
                lastTotalRub = sum;
            }
            totalSumView.setText(getString(R.string.cart_total_format, sum != null ? sum : "0.00"));
        });

        checkoutButton.setOnClickListener(v -> startCheckout());
    }

    private void startCheckout() {
        if (!checkoutButton.isEnabled()) {
            return;
        }
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.checkout_payment_title)
                .setItems(new CharSequence[]{
                        getString(R.string.checkout_payment_card),
                        getString(R.string.checkout_payment_cash)
                }, (dialog, which) -> {
                    if (which == 0) {
                        showCardCheckoutDialog();
                    } else {
                        showCashConfirmDialog();
                    }
                })
                .setNegativeButton(R.string.checkout_cancel, null)
                .show();
    }

    private void showCashConfirmDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.checkout_cash_confirm_title)
                .setMessage(getString(R.string.checkout_cash_confirm_message, lastTotalRub))
                .setPositiveButton(android.R.string.ok, (d, w) -> {
                    viewModel.clearCart();
                    Toast.makeText(requireContext(), R.string.checkout_success, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.checkout_cancel, null)
                .show();
    }

    /**
     * Демо-оплата картой: ввод только для UI. Не сохранять и не отправлять данные карты
     * (PCI и платёжный провайдер нужны для реальной оплаты).
     */
    private void showCardCheckoutDialog() {
        View form = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_checkout_card, null, false);
        TextInputEditText inputNumber = form.findViewById(R.id.input_card_number);
        TextInputEditText inputExpiry = form.findViewById(R.id.input_card_expiry);
        TextInputEditText inputCvc = form.findViewById(R.id.input_card_cvc);
        attachExpiryMmYyFormatting(inputExpiry);

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.checkout_card_dialog_title)
                .setView(form)
                .setPositiveButton(R.string.checkout_card_pay, null)
                .setNegativeButton(R.string.checkout_cancel, null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String rawNumber = inputNumber.getText() != null ? inputNumber.getText().toString() : "";
            String digitsNumber = rawNumber.replaceAll("\\D", "");
            String expiry = inputExpiry.getText() != null ? inputExpiry.getText().toString().trim() : "";
            String cvc = inputCvc.getText() != null ? inputCvc.getText().toString().trim() : "";

            if (digitsNumber.length() != 16) {
                Toast.makeText(requireContext(), R.string.checkout_validation_card_number, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isValidExpiry(expiry)) {
                Toast.makeText(requireContext(), R.string.checkout_validation_expiry, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!cvc.matches("\\d{3,4}")) {
                Toast.makeText(requireContext(), R.string.checkout_validation_cvc, Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.clearCart();
            Toast.makeText(requireContext(), R.string.checkout_success, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }));

        dialog.show();
    }

    /**
     * Автоматически вставляет «/» после месяца (MM/YY), принимает и ввод только цифрами.
     */
    private static void attachExpiryMmYyFormatting(TextInputEditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            private boolean selfChange;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (selfChange) {
                    return;
                }
                String digits = s.toString().replaceAll("\\D", "");
                if (digits.length() > 4) {
                    digits = digits.substring(0, 4);
                }
                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < digits.length(); i++) {
                    if (i == 2) {
                        formatted.append('/');
                    }
                    formatted.append(digits.charAt(i));
                }
                selfChange = true;
                editText.setText(formatted.toString());
                try {
                    editText.setSelection(formatted.length());
                } catch (Exception ignored) {
                }
                selfChange = false;
            }
        });
    }

    private static boolean isValidExpiry(String expiry) {
        if (TextUtils.isEmpty(expiry)) {
            return false;
        }
        String digits = expiry.replaceAll("\\D", "");
        if (digits.length() != 4) {
            return false;
        }
        int month = Integer.parseInt(digits.substring(0, 2));
        return month >= 1 && month <= 12;
    }

    @Override
    public void onIncrease(CartItemEntity item) {
        viewModel.increaseQuantity(item);
    }

    @Override
    public void onDecrease(CartItemEntity item) {
        viewModel.decreaseQuantity(item);
    }

    @Override
    public void onDelete(CartItemEntity item) {
        viewModel.removeFromCart(item);
    }
}
