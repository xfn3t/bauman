package transaction;

import java.time.LocalDateTime;

public record Transaction(double amount, String fromAccountNumber, String toAccountNumber, LocalDateTime timestamp,
						  boolean success, String message, TransactionType transactionType) {

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Type: ").append(transactionType).append("\n");
		if (fromAccountNumber != null) {
			sb.append("From: ").append(fromAccountNumber).append("\n");
		}
		if (toAccountNumber != null) {
			sb.append("To: ").append(toAccountNumber).append("\n");
		}
		sb.append("Amount: ").append(amount).append("\n");
		sb.append("Message: ").append(message).append("\n");
		sb.append("Time: ").append(timestamp).append("\n");
		sb.append("Success: ").append(success).append("\n");
		return sb.toString();
	}

	public static class Builder {

		private double amount;
		private String fromAccountNumber;
		private String toAccountNumber;
		private LocalDateTime timestamp = LocalDateTime.now();
		private boolean success = false;
		private String message = "";
		private TransactionType transactionType;

		public Builder amount(double amount) {
			this.amount = amount;
			return this;
		}

		public Builder transactionType(TransactionType transactionType) {
			this.transactionType = transactionType;
			return this;
		}

		public Builder fromAccount(String fromAccountNumber) {
			this.fromAccountNumber = fromAccountNumber;
			return this;
		}

		public Builder toAccount(String toAccountNumber) {
			this.toAccountNumber = toAccountNumber;
			return this;
		}

		public Builder timestamp(LocalDateTime timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public Builder success(boolean success) {
			this.success = success;
			return this;
		}

		public Builder message(String message) {
			this.message = message;
			return this;
		}

		public Transaction build() {
			return new Transaction(
					this.amount,
					this.fromAccountNumber,
					this.toAccountNumber,
					this.timestamp,
					this.success,
					this.message,
					this.transactionType
			);
		}
	}
}
