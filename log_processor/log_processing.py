search_total = 0
search_count = 0
f=open("log_search.txt", "r")
for x in f:
    search_total += int(x)
    search_count += 1
search_avg = search_total/search_count
print("Search Average: " + str(search_avg))


jdbc_total = 0
jdbc_count = 0
f=open("log_jdbc.txt", "r")
for x in f:
    jdbc_total += int(x)
    jdbc_count += 1
jdbc_avg = jdbc_total/jdbc_count
print("JDBC Average: " + str(jdbc_avg))
