
get "/", redirect: '/guess/'
get "/guess/", forward: "/genderGuess.groovy"
get "/guess/@name.@format", forward: "/genderGuess.groovy?name=@name&format=@format"
get "/guess/@name", forward: "/genderGuess.groovy?name=@name&format=json"

get "/favicon.ico", redirect: "/images/gaelyk-small-favicon.png"
