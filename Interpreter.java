package lox;

/*
	Interpreter class that evaluates expressions from the AST
	Class follows the visitor pattern to bypass messy self-interpretation logic
*/

class Interpreter implements Expr.Visitor<Object> {
	
	/* 
		The Interpretor's API
	*/
	void interpret(Expr expression) {
		try {
			Object value = evaluate(expression);
			System.out.println(stringify(value));
		} catch (RuntimeError error) {
			Lox.runtimeError(error);
		}
	}
	//Evaluates literals
	@Override
	public Object visitLiteralExpr (Expr.Literal expr) {
		return expr.value;
	}

	//Evaluates parentheses
	@Override
	public Object visitGroupingExpr (Expr.Grouping expr) {
		return evaluate(expr.expression);
	}

	/* 
		Evaluates unary expressions
		Two Unary Operators: ! and -
		Note in (-) : Because we don't know the type statically, we cast the object as
		a double during runtime. This is the core of dynamic typed languages.
	*/
	@Override
	public Object visitUnaryExpr(Expr.Unary expr) {
		Object right = evaluate(expr.right);
		
		switch(expr.operator.type) {
			case MINUS:
				checkNumberOperand(expr.operator, right);
				return -(double)right;
			case BANG:
				return !isTruthy(right);
		}
		//Expression unreachable
		return null;
	}

	//Evaluate binary operators
	@Override
	public Object visitBinaryExpr(Expr.Binary expr) {
		Object left = evaluate(expr.left);
		Object right = evaluate(expr.right);

		switch(expr.operator.type) {
			case GREATER:
				checkNumberOperands(expr.operator, left, right);
				return (double)left > (double)right;
			case GREATER_EQUAL:
				checkNumberOperands(expr.operator, left, right);
				return (double)left >= (double)right;
			case LESS:
				checkNumberOperands(expr.operator, left, right);
				return (double)left < (double)right;
			case LESS_EQUAL:
				checkNumberOperands(expr.operator, left, right);
				return (double)left <= (double)right;

			case BANG_EQUAL:
				checkNumberOperands(expr.operator, left, right);
				return !isEqual(left, right);
			case EQUAL_EQUAL:
				checkNumberOperands(expr.operator, left, right);
				return isEqual(left, right);

			case MINUS:
				checkNumberOperands(expr.operator, left, right);
				return (double)left - (double)right;
			case SLASH:
				checkNumberOperands(expr.operator, left, right);
				return (double)left / (double)right;
			case STAR:
				checkNumberOperands(expr.operator, left, right);
				return (double)left * (double)right;
			case PLUS:
				if (left instanceof Double && right instanceof Double) {
					return (double)left + (double)right;
				}
				if(left instanceof String && right instanceof String) {
					return (String)left + (String)right;
				}
				throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");

		}
	
		//Expr unreachable
		return null;
	}


	//Helper method to evaluate nested subexpressions
	private Object evaluate(Expr expr) {
		return expr.accept(this);
	}

	//check if operand is a number
	private void checkNumberOperand(Token operator, Object operand) {
		if (operand instanceof Double) return;
		throw new RuntimeError(operator, "Operand must be a number.");
	}

	private void checkNumberOperands(Token operator, Object left, Object right) {
		if (left instanceof Double && right instanceof Double) return;
		throw new RuntimeError(operator, "Operands must be numbers.");
	}
	
	//Equality logic for binary expr
	private boolean isEqual(Object a, Object b) {
		if (a == null && b == null) return true;
		if (a == null) return false;
		
		return a.equals(b);
	}
	
	private String stringify(Object object) {
		if (object == null) return "nil";
		
		if (object instanceof Double) {
			String text = object.toString();
			if (text.endsWith(".0")) {
				text = text.substring(0, text.length() - 2);
			}
			return text;
		}
		
		return object.toString();
	}

	/*
		This method determines the "truthy"ness of an object, where the object type
		becomes a boolean condition.
		This is language specific, but following CraftingInterpreters, we'll follow
		Ruby's rules of non-null objects being truthy, and null objects being falsey.
		If object is boolean, then we follow its boolean state like normal
	*/
	private boolean isTruthy(Object obj) {
		if (obj == null) return false;
		if (obj instanceof Boolean) return (boolean)obj;
		return true;
	}
}
