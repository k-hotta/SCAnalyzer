sim[[1]] <- NULL
rev <- t(sim)
numColumns <- ncol(rev)
numVersions <- nrow(rev)

for (i in c(1:numColumns)) {
	plot(rev[,i], xlim=c(1,numVersions), ylim=c(0,2), type="l", ylab="")
	par(new=T)
}