package org.erlide.wrangler.refactoring.core.renamefunction;

import org.erlide.jinterface.rpc.RpcException;
import org.erlide.jinterface.rpc.RpcResult;
import org.erlide.runtime.backend.exceptions.ErlangRpcException;
import org.erlide.wrangler.refactoring.core.RefactoringParameters;
import org.erlide.wrangler.refactoring.core.rename.RenameRefactoring;

import com.ericsson.otp.erlang.OtpErlangList;

public class RenameFunctionRefactoring extends RenameRefactoring {

	public RenameFunctionRefactoring(RefactoringParameters parameters) {
		super(parameters);
	}

	@Override
	public String getName() {
		return "Rename function";
	}

	@SuppressWarnings("boxing")
	@Override
	protected RpcResult sendRPC(String filePath, OtpErlangList searchPath)
			throws ErlangRpcException, RpcException {
		return managedBackend.call("wrangler", "rename_fun_eclipse", "siisxi",
				filePath, parameters.getStartLine(), parameters
						.getStartColumn(), newName, searchPath, parameters
						.getEditorTabWidth());
	}
}
