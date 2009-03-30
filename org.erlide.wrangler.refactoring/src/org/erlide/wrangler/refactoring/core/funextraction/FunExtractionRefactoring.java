package org.erlide.wrangler.refactoring.core.funextraction;

import org.erlide.jinterface.rpc.RpcException;
import org.erlide.jinterface.rpc.RpcResult;
import org.erlide.runtime.backend.exceptions.ErlangRpcException;
import org.erlide.wrangler.refactoring.core.RefactoringParameters;
import org.erlide.wrangler.refactoring.core.WranglerRefactoring;

import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangTuple;

public class FunExtractionRefactoring extends WranglerRefactoring {

	public FunExtractionRefactoring(RefactoringParameters parameters) {
		super(parameters);
	}

	@Override
	public String getName() {
		return "Fun extraction";
	}

	@SuppressWarnings("boxing")
	@Override
	protected RpcResult sendRPC(String filePath, OtpErlangList searchPath)
			throws ErlangRpcException, RpcException {
		OtpErlangTuple startPos = createPos(parameters.getStartLine(),
				parameters.getStartColumn());
		OtpErlangTuple endPos = createPos(parameters.getEndLine(), parameters
				.getEndColumn());
		return managedBackend.call("wrangler", "fun_extraction_eclipse",
				"sxxsi", filePath, startPos, endPos, newName, parameters
						.getEditorTabWidth());
	}

}
