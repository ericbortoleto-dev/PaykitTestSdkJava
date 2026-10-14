package com.linx.paykit.tef.data;

import com.linx.paykit.common.ui.InputParameters;
import java.util.List;
import java.util.UUID;

public interface PaykitCallbackState {

    class Initial implements PaykitCallbackState {}

    class Alert implements PaykitCallbackState {
        private final String message;

        public Alert(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    class Menu implements PaykitCallbackState {
        private final String label;
        private final List<String> items;

        public Menu(String label, List<String> items) {
            this.label = label;
            this.items = items;
        }

        public String getLabel() {
            return label;
        }

        public List<String> getItems() {
            return items;
        }
    }

    class Confirmation implements PaykitCallbackState {
        private final String message;

        public Confirmation(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    class Input implements PaykitCallbackState {
        private final InputParameters parameters;

        public Input(InputParameters parameters) {
            this.parameters = parameters;
        }

        public InputParameters getParameters() {
            return parameters;
        }
    }

    class QrCode implements PaykitCallbackState {
        private final String message;
        private final String qrCode;
        private final int timeout;

        public QrCode(String message, String qrCode, int timeout) {
            this.message = message;
            this.qrCode = qrCode;
            this.timeout = timeout;
        }

        public String getMessage() {
            return message;
        }

        public String getQrCode() {
            return qrCode;
        }

        public int getTimeout() {
            return timeout;
        }
    }

    class Error implements PaykitCallbackState {
        private final String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    class Message implements PaykitCallbackState {
        private final String message;

        public Message(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    class Processing implements PaykitCallbackState {
        private final String message;

        public Processing(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    class Password implements PaykitCallbackState {
        private final String message;

        public Password(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    class CardInstruction implements PaykitCallbackState {
        private final String message;
        private final CardInstructionType type;
        private final String id;

        public CardInstruction(String message, CardInstructionType type) {
            this.message = message;
            this.type = type;
            this.id = UUID.randomUUID().toString();
        }

        public String getMessage() {
            return message;
        }

        public CardInstructionType getType() {
            return type;
        }

        public String getId() {
            return id;
        }
    }

    enum CardInstructionType {
        INSERT, REMOVE
    }
}
